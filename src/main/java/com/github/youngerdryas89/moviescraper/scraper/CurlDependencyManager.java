package com.github.youngerdryas89.moviescraper.scraper;

import com.github.youngerdryas89.moviescraper.SystemInfo;
import io.vavr.control.Try;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
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

public class CurlDependencyManager {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Path curl;


    public CurlDependencyManager() {
        if(Files.notExists(getDataDirectory()))
            Try.of(() -> Files.createDirectories(getDataDirectory())).onFailure((Void) -> System.err.println("Error: Failed to create data directory: " + getDataDirectory()));
    }

    public Future<Option<Path>> get() {
        return executor.submit(() -> {
            try {
                if (!checkForCurl()) {
                    var pathOfStatusCode = new GetCurlOp().get();
                    if(pathOfStatusCode.isRight()) {
                        var temp = copyDeps(pathOfStatusCode.right().get());
                        curl = temp.getOrNull();
                        return temp;
                    } else {
                        System.err.println("Error: Failed to download libcurl-impersonate; status code: " + pathOfStatusCode.left().toString());
                        return Option.none();
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println(Arrays.toString(e.getStackTrace()));
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
            return Option.none();
        });
    }
    Path getDataDirectory() {
        if(IS_OS_WINDOWS) {
            return Path.of(System.getenv("APPDATA") + "\\JAVMovieScraper");
        } else if(IS_OS_LINUX) {
            return Path.of(System.getenv("HOME") + "/.local/share/JAVMovieScraper");
        }
        return Path.of("").toAbsolutePath();
    }

    private boolean checkForCurl() throws Exception {
        if(IS_OS_WINDOWS) {
            return Files.exists(getDataDirectory().resolve("libcurl.dll")) && Files.exists(getDataDirectory().resolve("zlib.dll"));
        }else if (IS_OS_LINUX){
            return Files.exists(getDataDirectory().resolve("libcurl-impersonate.so.4.8.0"));
        }
        return false;
    }

    private Option<Path> copyDeps(Path path){
        try {
            if (IS_OS_WINDOWS) {
                Files.copy(path.resolve("bin").resolve("libcurl.dll"), getDataDirectory().resolve("libcurl.dll"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(path.resolve("bin").resolve("zlib.dll"), getDataDirectory().resolve("zlib.dll"), StandardCopyOption.REPLACE_EXISTING);
                return Option.of(getDataDirectory().resolve("libcurl.dll"));
            } else if (IS_OS_LINUX) {
                Files.copy(path.resolve("libcurl-impersonate.so.4.8.0"), getDataDirectory().resolve("libcurl-impersonate.so.4.8.0"), StandardCopyOption.REPLACE_EXISTING);
                return Option.of(getDataDirectory().resolve("libcurl-impersonate.so.4.8.0"));
            }

        } catch (Exception e){
            System.err.println("Error: Failed to copy libcurl impersonate!");
            e.printStackTrace();
            System.err.println(e.getMessage());
            return Option.none();
        }
        return Option.none();
    }
}

class GetCurlOp {
    private String url_ = "https://github.com/lexiforest/curl-impersonate/releases/download/v$version/libcurl-impersonate-v$version.$arch-$platform.tar.gz";
    private String version = "1.0.0";

    public Either<Integer, Path> get() throws Exception {
        var tempDir = Files.createTempDirectory("jmsTmp");
        var url = url_.replace("$version", version).replace("$arch", getArchitecture()).replace("$platform", getPlatform());

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", getRandomUserAgent());

            httpClient.execute(request, response -> {
               int statusCode = response.getStatusLine().getStatusCode();
               System.out.println(statusCode);
               if(statusCode == 200){
                   try(InputStream is = response.getEntity().getContent()){
                       extractTarGz(is, tempDir);
                       return Either.right(tempDir);
                   }
               }
                return Either.left(statusCode);
            });
        }

        return Either.right(tempDir);
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
