package bot.pac;

public class QuestClass {

    private int id;
    private String name;
    private String photo;
    private String description;
    private String contacts;
    private String location;

    public QuestClass() {
    }

    public QuestClass(int id, String name, String description, String photo, String contacts, String location) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.description = description;
        this.contacts = contacts;
        this.location = location;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + photo.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + contacts.hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDescription() {
        return description;
    }

    public String getContacts() {
        return contacts;
    }

    public String getLocation() {
        return location;
    }
}
