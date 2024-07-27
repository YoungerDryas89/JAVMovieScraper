package moviescraper.doctord.view;

public interface FileListListener {

    public void preHandleSelectedReferences(FileList parent);
    public void handleSelectedReferences(FileList parent);
    public void preUpdate(FileList parent);
    public void Updated(FileList parent);
}
