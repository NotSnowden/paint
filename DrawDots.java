import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.util.Random;

public class DrawDots implements MouseInputListener  {
    JFrame frame;
    JPanel drawPanel, inputPanel, mainPanel;
    JLabel label;
    JButton radiusBtn, shapeBtn, colorBtn, eraserBtn, resetBtn;
    Icon dimensionIcon, shapeIcon, colorIcon, eraserIcon, resetIcon;
    Font font;
    int oldx, oldy, x, y, radius = 10;
    boolean reset = false, eraser = false;
    Color color = Color.BLACK;
    Random rand = new Random();
    String[] shapes = { "linea", "quadrato vuoto", "cerchio vuoto", "quadrato pieno", "cerchio pieno" };
    String shape = "linea";

    public DrawDots() {
        frame = new JFrame("Paint tarocco");
        font = new Font(Font.SERIF, Font.BOLD, 30);

        mainPanel = new JPanel(new BorderLayout());
        
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        inputPanel.setBackground(Color.lightGray);

        GridBagConstraints c = new GridBagConstraints();
        
        drawPanel = new GPanel();
        drawPanel.addMouseListener(this);
        drawPanel.addMouseMotionListener(this);

        label = new JLabel("Benvenuto nel Paint tarocco!");  
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.CENTER);

        dimensionIcon = new ImageIcon("media/dimension.png");
        radiusBtn = new JButton(dimensionIcon);

        shapeIcon = new ImageIcon("media/shape.png");
        shapeBtn = new JButton(shapeIcon);

        colorIcon = new ImageIcon("media/color.png");
        colorBtn = new JButton(colorIcon);

        eraserIcon = new ImageIcon("media/eraser.png");
        eraserBtn = new JButton(eraserIcon);

        resetIcon = new ImageIcon("media/reset.png");
        resetBtn = new JButton(resetIcon);

        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 5;
        c.insets = new Insets(5, 5, 5, 5);
        inputPanel.add(label, c);

        c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(radiusBtn, c);

        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(shapeBtn, c);

        c.gridx = 2;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(colorBtn, c);

        c.gridx = 3;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(eraserBtn, c);

        c.gridx = 4;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(resetBtn, c);

        mainPanel.add(inputPanel, BorderLayout.PAGE_START);
        mainPanel.add(drawPanel, BorderLayout.CENTER);

        radiusBtn.addActionListener(e -> setDimension());
        shapeBtn.addActionListener(e -> setShape());
        colorBtn.addActionListener(e -> setColor());
        eraserBtn.addActionListener(e -> enableEraser());
        resetBtn.addActionListener(e -> resetPanel());

        frame.add(mainPanel);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(mainPanel, "Sicuro di voler chiudere Paint tarocco?");
                
                if (confirm == 0)
                    System.exit(0);
            }
        });        
        
        frame.setMinimumSize(new Dimension(800, 500));
        frame.pack();
        //the screen is positioned in the center of the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //unlike other languages, java sets integers variables equal to zero by default
    public void setDimension() {
        String input = JOptionPane.showInputDialog(mainPanel, "Inserisci le dimensioni!", 
                "Raggio", JOptionPane.INFORMATION_MESSAGE);

        if (input == null)
            return;

        try {
            radius = Integer.parseInt(input);

            if (radius < 1 || radius > 100000) {
                radius = 10;
                label.setText("We campione, mi stai mettendo alla prova?");
                return;
            }
        }
        catch (Exception ex) {
            label.setText("Inserire un numero valido");
            return;
        }                
    }

    public void setShape() {
        shape = (String)JOptionPane.showInputDialog(mainPanel, "Scegli la forma!", 
        "Forma", JOptionPane.INFORMATION_MESSAGE, null, shapes, shape);

        if (shape == null)
            shape = "linea";
    }

    public void setColor() {
        Color input = JColorChooser.showDialog(mainPanel, "Scegli il colore!", color);
        
        if (input == null)
            return;

        color = input;
        label.setText("Benvenuto nel Paint tarocco!");
        eraser = false;
    }

    public void enableEraser() {
        label.setText("Modalità gomma attiva");
        eraser = true;
        drawPanel.repaint();
    }

    public void resetPanel() {
        reset = true;
        drawPanel.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //eraser is also in the condition since we need to know if the
        //user is dragging with the eraser tool enabled
        if (!shape.equals("linea") && !eraser)
            return;

        oldx = x;
        oldy = y;
        x = e.getX();
        y = e.getY();
        drawPanel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x = 0;
        y = 0;
        oldx = 0;
        oldy = 0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //if shape is setted to "line" or the eraser tool is enabled, when the user
        //clicks do nothing
        if (shape.equals("linea") || eraser)
            return;

        x = e.getX();
        y = e.getY();
        drawPanel.repaint();
    }

    class GPanel extends JPanel {
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;

            if (reset) {
                super.paint(g);
                reset = false;
                return;
            }

            if (x == 0 && y == 0)
                return;

            if (eraser) {
                if (oldx == 0 && oldy == 0)
                    return;
                
                g.setColor(drawPanel.getBackground());
                g.fillOval(x - (radius / 2), y - (radius / 2), radius, radius);
                g2d.setStroke(new BasicStroke(radius));
                g2d.drawLine(oldx, oldy, x, y);
                return;
            }

            g.setColor(color);
            
            switch (shape) {
                case "linea":
                    if (oldx == 0 && oldy == 0)
                        return;
                    
                    //if the user moves the mouse too fast, there will be empty space between each point.
                    //in order to fix this, I draw a line between each point
                    g.fillOval(x - (radius / 2), y - (radius / 2), radius, radius);
                    g2d.setStroke(new BasicStroke(radius));
                    g2d.drawLine(oldx, oldy, x, y);
                    break;

                case "quadrato vuoto":
                    g.drawRect(x - (radius / 2), y - (radius / 2), radius, radius);
                    x = 0;
                    y = 0;
                    break;

                case "cerchio vuoto":
                    g.drawOval(x - (radius / 2), y - (radius / 2), radius, radius);
                    x = 0;
                    y = 0;
                    break;

                case "quadrato pieno":
                    g.fillRect(x - (radius / 2), y - (radius / 2), radius, radius);
                    x = 0;
                    y = 0;
                    break;

                case "cerchio pieno":
                    g.fillOval(x - (radius / 2), y - (radius / 2), radius, radius);
                    x = 0;
                    y = 0;
            }
        }
    };

    @Override
    public void mouseEntered(MouseEvent e) {        
    }

    @Override
    public void mouseExited(MouseEvent e) {        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {        
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
    }

    public static void main(String[] args) {
        new DrawDots();
    }
}
