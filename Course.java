public class Course {
    private int id;
    private String name;
    private String instructor;

    public Course() {}

    public Course(String name, String instructor) {
        this.name = name;
        this.instructor = instructor;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getInstructor() { return instructor; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
}
