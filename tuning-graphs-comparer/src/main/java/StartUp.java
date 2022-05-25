import com.cyecize.javache.embedded.JavacheEmbedded;
import com.cyecize.summer.DispatcherSolet;

public class StartUp extends DispatcherSolet {
    public static void main(String[] args) {
        JavacheEmbedded.startServer(StartUp.class);
    }
}
