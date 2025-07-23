public class Feedback {
    private int id;
    private int studentId;
    private int courseId;
    private int rating;
    private String comments;

    public Feedback() {}

    public Feedback(int studentId, int courseId, int rating, String comments) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.rating = rating;
        this.comments = comments;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public int getRating() { return rating; }
    public String getComments() { return comments; }

    public void setId(int id) { this.id = id; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComments(String comments) { this.comments = comments; }
}
