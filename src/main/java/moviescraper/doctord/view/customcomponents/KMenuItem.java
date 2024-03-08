package moviescraper.doctord.view.customcomponents;

import javax.swing.*;

/**
 * This class exists just so I can just change the icon
 */
public class KMenuItem extends JMenuItem {
    void setIcon(ImageIcon icon){
        init(getText(), icon);
    }
}
