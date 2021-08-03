package extension;

import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionFormLauncher;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.extra.tools.PacketInfoSupport;
import gearth.ui.GEarthController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

@ExtensionInfo(
        Title =         "Custom Walk",
        Description =   "Continuously sit, perform actions and use signs while walking",
        Version =       "0.1",
        Author =        "WiredSpast"
)
public class CustomWalk extends ExtensionForm {
    // FX-Components
    public CheckBox sitBox;
    public ChoiceBox<Sign> signsBox;
    public ChoiceBox<Action> actionsBox;
    public Button enableButton;

    private PacketInfoSupport packetInfoSupport;
    private boolean enabled = false;

    public static void main(String[] args) {
        ExtensionFormLauncher.trigger(CustomWalk.class, args);
    }

    @Override
    public ExtensionForm launchForm(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(CustomWalk.class.getClassLoader().getResource("fxml/customwalk.fxml"));
        Parent root = loader.load();

        stage.setTitle("Custom Walk");
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add(Objects.requireNonNull(GEarthController.class.getResource("/gearth/ui/bootstrap3.css")).toExternalForm());
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("/images/duck_icon.png")).openStream()));

        stage.setResizable(false);

        return loader.getController();
    }

    @Override
    protected void initExtension() {
        packetInfoSupport = new PacketInfoSupport(this);

        signsBox.getItems().addAll(Sign.values());
        signsBox.setValue(Sign.NONE);

        actionsBox.getItems().addAll(Action.values());
        actionsBox.setValue(Action.NONE);

        updateUI();
        new Thread(this::mainLoop).start();
    }

    private void updateUI() {
        if(enabled) {
            enableButton.setText("Disable");
        } else {
            enableButton.setText("Enable");
        }
    }

    @Override
    protected void onEndConnection() {
        enabled = false;
    }

    private void mainLoop() {
        while(true) {
            if(enabled) {
                if(sitBox.isSelected()) {
                    packetInfoSupport.sendToServer("ChangePosture", 1);
                }

                if(!actionsBox.getValue().equals(Action.NONE)) {
                    packetInfoSupport.sendToServer("AvatarExpression", actionsBox.getValue().value);
                }

                if(!signsBox.getValue().equals(Sign.NONE)) {
                    packetInfoSupport.sendToServer("Sign", signsBox.getValue().value);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onEnableButton(ActionEvent actionEvent) {
        enabled = !enabled;
        updateUI();
    }

    private enum Sign {
        NONE("None", -1),
        ZERO("0", 0),
        ONE("1", 1),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        HEART("Heart", 11),
        SKULL("Skull", 12),
        EXCLAMATIONPOINT("Exclamation point", 13),
        BALL("Ball", 14),
        SMILEY("Smiley", 15),
        REDCARD("Red card", 16),
        YELLOWCARD("Yellow card", 17);

        public final String name;
        public final int value;

        Sign(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum Action {
        NONE("None", -1),
        WAVE("Wave", 1),
        KISS("Blow kiss (HC)", 2),
        LAUGH("Laugh (HC)", 3),
        JUMP("Jump (HC)", 6),
        THUMBSUP("Thumbs up", 7);

        public final String name;
        public final int value;

        Action(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
