package client.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

class ViewToolbox
{
    private ViewToolbox()
    { }

    static Scene buildSceneFromGridPane(GridPane gridPane)
    {
        Group root = new Group();
        root.getChildren().addAll(gridPane);
        return new Scene(root);
    }

    static GridPane buildStandardGridPane()
    {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(5));

        return gridPane;
    }
}
