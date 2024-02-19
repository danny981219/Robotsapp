package bg.tu.robot;

public class ISMain {

    private static int BASE_PORT = 6000;

    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            new ISThread(BASE_PORT + i, i).start();
        }
    }

}
