package Server.FileManager;

public class FileEntity {
    private int id;
    private String name;
    private String memeType;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemeType() {
        return memeType;
    }

    public void setMemeType(String memeType) {
        this.memeType = memeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
