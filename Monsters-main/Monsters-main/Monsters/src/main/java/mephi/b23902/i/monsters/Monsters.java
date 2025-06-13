
package mephi.b23902.i.monsters;

import controller.MonsterController;
import javax.swing.SwingUtilities;
import view.MonsterView;

public class Monsters {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MonsterController controller = new MonsterController();
            MonsterView view = new MonsterView(controller);
            view.setVisible(true);
        });
    }
}