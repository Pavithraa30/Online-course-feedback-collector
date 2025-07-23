import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FeedbackService fs = new FeedbackService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Feedback System Menu ===");
            System.out.println("1. Submit Feedback");
            System.out.println("2. Admin Login & View Report");
            System.out.println("3. Export Feedback to CSV");
            System.out.println("4. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1 -> fs.submitFeedback();
                case 2 -> {
                    if (fs.adminLogin()) fs.viewFeedbackReport();
                    else System.out.println("❌ Invalid login.");
                }
                case 3 -> fs.exportFeedbackToCSV();
                case 4 -> {
                    System.out.println("Thank you!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
