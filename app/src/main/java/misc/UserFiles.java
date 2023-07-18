package misc;

public class UserFiles {
    int file_type;
    int id;
    String file_path;
    int user_id;

    String title;

    String description;

    String tags;
    public UserFiles(int file_type, int id, String file_path,
                     String title,String description,String tags,int user_id) {
        this.file_type = file_type;
        this.id = id;
        this.file_path = file_path;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserFiles{" +
                "file_type=" + file_type +
                ", id=" + id +
                ", file_path='" + file_path + '\'' +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
