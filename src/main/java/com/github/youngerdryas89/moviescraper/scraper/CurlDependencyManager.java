package com.github.youngerdryas89.moviescraper.scraper;

import com.github.youngerdryas89.moviescraper.SystemInfo;
import io.vavr.control.Try;
import net.covers1624.curl4j.CABundle;
import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.httpapi.Curl4jHttpEngine;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import io.vavr.control.Option;
import io.vavr.control.Either;
import static com.github.youngerdryas89.moviescraper.scraper.UserAgent.getRandomUserAgent;
import static org.apache.commons.lang3.ArchUtils.getProcessor;
import static org.apache.commons.lang3.SystemUtils.*;

public class CurlDependencyManager implements AutoCloseable {
    private Path curl;
    private Path location;
    private Curl4jHttpEngine engine;

    public CurlDependencyManager() {
        if(Files.notExists(getDataDirectory()))
            location = Try.of(() -> Files.createDirectories(getDataDirectory()))
                    .getOrElseGet((Void) -> {
                        System.err.println("Warning: Failed to create data directory: " + getDataDirectory());
                        return Path.of(".");
                    });
        else
            location = getDataDirectory();
    }

    public Curl4jHttpEngine getEngine(){
        if(engine == null){
            engine = new Curl4jHttpEngine(CABundle.builtIn());
        }
        return engine;
    }


    public Path getCurlLocation(){
        return curl;
    }

    public boolean isAvailable(){
        return curl != null;
    }
    public Path getLocation() {
        return location;
    }

    public Either<CurlMError, Path> get() {
        var curlLoc = checkForCurl();
        if (curlLoc.isEmpty()) {
            var pathOfStatusCode = new GetCurlOp().get();
            if(pathOfStatusCode.isRight()) {
                var temp = copyDeps(pathOfStatusCode.right().get());
                CURL.setLibCurlName(temp.get().toString());
                engine = new Curl4jHttpEngine(CABundle.builtIn(), "firefox135");
                curl = temp.getOrNull();
                return Either.right(temp.get());
            } else if(pathOfStatusCode.isLeft()){
                return Either.left(pathOfStatusCode.left().get());
            }
        }
        return Either.right(curlLoc.get());
    }
    Path getDataDirectory() {
        if(IS_OS_WINDOWS) {
            return Path.of(System.getenv("APPDATA") + "\\JAVMovieScraper");
        } else if(IS_OS_LINUX) {
            return Path.of(System.getenv("HOME") + "/.local/share/JAVMovieScraper");
        } else if (IS_OS_MAC){
            return Path.of("~/Library/Application/JAVMovieScraper");
        }
        System.err.println("Warning: Unknown operating system: Using current working directory to store shared libraries");
        return Path.of("").toAbsolutePath();
    }

    private Option<Path> checkForCurl(){
        if(IS_OS_WINDOWS) {
            if(Files.exists(getDataDirectory().resolve("libcurl.dll")) && Files.exists(getDataDirectory().resolve("zlib.dll")))
                return Option.of(getDataDirectory().resolve("libcurl.dll"));
        }else if (IS_OS_LINUX){
            if(Files.exists(getDataDirectory().resolve("libcurl-impersonate.so.4.8.0")))
                return Option.of(location.resolve("libcurl-impersonate.so.4.8.0"));
        } else if(IS_OS_MAC){
            if(Files.exists(getDataDirectory().resolve("libcurl-impersonate.4.dylib"))){
                return Option.of(location.resolve("libcurl-impersonate.4.dylib"));
            }
        }
        return Option.none();
    }

    private Option<Path> copyDeps(Path path){
        try {
            if (IS_OS_WINDOWS) {
                Files.copy(path.resolve("bin").resolve("libcurl.dll"), location.resolve("libcurl.dll"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(path.resolve("bin").resolve("zlib.dll"), location.resolve("zlib.dll"), StandardCopyOption.REPLACE_EXISTING);
                return Option.of(getDataDirectory().resolve("libcurl.dll"));
            } else if (IS_OS_LINUX) {
                Files.copy(path.resolve("libcurl-impersonate.so.4.8.0"), location.resolve("libcurl-impersonate.so.4.8.0"), StandardCopyOption.REPLACE_EXISTING);
                return Option.of(getDataDirectory().resolve("libcurl-impersonate.so.4.8.0"));
            } else if(IS_OS_MAC){
                Files.copy(path.resolve("libcurl-impersonate.4.dylib"), location.resolve("libcurl-impersonate.4.dylib"));
                return Option.of(location.resolve("libcurl-impersonate.4.dylib"));
            }

        } catch (Exception e){
            System.err.println("Error: Failed to copy libcurl impersonate!");
            e.printStackTrace();
            System.err.println(e.getMessage());
            return Option.none();
        }
        return Option.none();
    }

    public String Version() {
        return "1.0.0";
    }

    @Override
    public void close() {
        engine.close();
    }
}

class GetCurlOp {
    private String url_ = "https://github.com/lexiforest/curl-impersonate/releases/download/v$version/libcurl-impersonate-v$version.$arch-$platform.tar.gz";
    private String version = "1.0.0";

    public Either<CurlMError, Path> get()  {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
            var tempDir = Files.createTempDirectory("jmsTmp");
            var url = url_.replace("$version", version).replace("$arch", getArchitecture()).replace("$platform", getPlatform());
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", getRandomUserAgent());

            httpClient.execute(request, response -> {
               int statusCode = response.getStatusLine().getStatusCode();
               if(statusCode == 200){
                   try(InputStream is = response.getEntity().getContent()){
                       extractTarGz(is, tempDir);
                       return Either.right(tempDir);
                   }catch (IOException e){
                       return Either.left(new CurlMError.CurlDependencyManagerError(
                               "Error: Failed to failed to extract libcurl-impersonate; Exception: " + e.getMessage()
                       ));
                   }
               }
                return Either.left(new CurlMError.HTTPError(response.getStatusLine().getReasonPhrase(), statusCode));
            });
            return Either.right(tempDir);
        } catch (Exception e){
            return Either.left(new CurlMError.CurlDependencyManagerError("Error: In CurlDependencyManager exception: " + e.getMessage()));
        }

    }

    void extractTarGz(InputStream inputStream, Path destination) throws IOException {
        try(TarArchiveInputStream tio = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(inputStream)))){
            ArchiveEntry entity;
            while ((entity = tio.getNextEntry()) != null){
                Path pathEntryObject = destination.resolve(entity.getName());
                if (!tio.canReadEntryData(entity)) {

                    continue;
                }

                if(entity.isDirectory()){
                    Files.createDirectories(pathEntryObject);
                } else {
                    Files.createDirectories(pathEntryObject.getParent());
                    Files.copy(tio, pathEntryObject);
                }
            }
        }
    }

    String getArchitecture() throws Exception {
        var os = getPlatform();
        var processorType = getProcessor().getType();
        if(os.equals("win32")){
            return switch (getProcessor().getArch()) {
                case BIT_64 -> "x86_64";
                case BIT_32 -> "i686";
                default -> throw new IllegalStateException("Unknown/Unsupported processor type!");
            };
        } else if (os.equals("linux-gnu")){
            return switch (processorType){
                case X86: {
                    yield switch (getProcessor().getArch()){
                        case BIT_32 -> "i386";
                        case BIT_64 -> "x86_64";
                        default -> throw new IllegalStateException("Unknown/Unsupported processor type!");
                    };
                }
                default: throw new IllegalStateException("Unknown/Unsupported processor type!");
            };
        } else if (os.equals("macos")){
            return "x86_64";
        }
        return "x86_64";
    }

    String getPlatform() throws Exception {
        if (IS_OS_WINDOWS) {
            return "win32";
        } else if(IS_OS_LINUX) {
            return "linux-gnu";
        } else if(IS_OS_MAC) {
            return "macos";
        } else {
            throw new Exception("Unsupported Operating System!");
        }
    }

}
