package all.rev;

import javax.swing.*;

class JTab extends JLabel {
    private int index;

    public JTab(String content, int index) {
        super(content);
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}