import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.util.ListIterator;

/**
 *
 * @author umrai002
 *
 */
public class Main {

    JPanel panel;
    JTextField iter;
    JTextField ang;
    BufferedImage img;
    JFrame frame;
    AffineTransform clockwise;
    AffineTransform anticlockwise;
    Vector<Point2D> path;
    public static final double SCALE_FACTOR=0.7071067811;

    private class drawArea extends JFrame {

        public drawArea(String title) {
            super(title);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            this.getGraphics().drawImage(img, 0, 50, null);
        }
    }

    public Main() {
        clockwise = new AffineTransform();
        anticlockwise = new AffineTransform();
        path = new Vector();
        frame = new drawArea("Levy Curve");
        panel = new JPanel();
        JLabel iterations = new JLabel("Iterations:");
        iterations.setBounds(0, 0, 100, 20);
        iter = new JTextField("14");
        iter.setBounds(100, 0, 100, 20);
        JLabel angle = new JLabel("Angle:");
        angle.setBounds(220, 0, 100, 20);
        ang = new JTextField("45"); //All the swing controls hard coded
        ang.setBounds(300, 0, 100, 20);
        JButton simulate = new JButton("simulate");
        simulate.setBounds(420, 0, 100, 20);
        simulate.addActionListener(new listener(this));
        panel.setBounds(0, 20, 1000, 580);
        img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);

         JButton save=new JButton("save");
         save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                ImageIO.write(img,"png",new File("fractal.png"));
                }
                catch(Exception ec){
                    System.out.println("Couldn't save image");
                }
            }
        });
        save.setBounds(530, 0, 100, 20);

 Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
        panel.setBounds(0, 20, size.width-20,size.height-30);
        img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        frame.setBounds(0, 0, size.width, size.height);
        frame.setLayout(null);
        frame.add(iterations);
        frame.add(iter);
        frame.add(panel);
        frame.add(angle);
        frame.add(ang);
        frame.add(simulate);
        frame.add(save);
        //frame.pack();
        frame.setVisible(true);
        draw();



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public class listener implements ActionListener {

        Main ma;

        public listener(Main m) {
            ma = m;
        }

        public void actionPerformed(ActionEvent e) {
            ma.draw();
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
    }

    private void draw() {
        int loops = Integer.parseInt(iter.getText());
        double curve = Double.parseDouble(ang.getText()) * Math.PI / 180;

	//This path vector contains all the points on the fractal
        path.clear();

	//Buffered Image tied to the JPanel
        img.flush();
        Graphics2D gr = img.createGraphics();
        gr.setColor(Color.black);
        gr.fillRect(0, 0, img.getWidth(), img.getHeight());
        frame.repaint();
        gr.setColor(Color.red);
        gr.setStroke(new BasicStroke(1));
        //Initializing with two points
        path.add(new Point2D.Double(300, 100));
        path.add(new Point2D.Double(700, 100));
        Line2D.Double line = new Line2D.Double(path.get(0), path.get(1));
        gr.draw(line);
        Point2D a;
        Point2D b;
        Point2D c,d;
        int col=65330;
        ListIterator it;
        it = path.listIterator();

        for (int j = 0; j < loops; j++) {
            it = path.listIterator();
            System.out.println("iteration=" + Integer.toString(j));

            try {
                //                Thread.sleep(1000);
            } catch (Exception e) {
            }
            while (it.hasNext()) {
                col+=10;
                clockwise = new AffineTransform();
                anticlockwise=new AffineTransform();
                if (it.hasNext()) {
                    a = (Point2D.Double) it.next();
                    if (it.hasNext()) {
                        b = (Point2D.Double) it.next();

                        gr.setColor(Color.black);
                        line.setLine(a, b);
                        gr.draw(line);
                        gr.setColor(new Color(col));


                        clockwise.rotate(curve, a.getX(), a.getY());

                        /*Each step consists of one rotation
                         * and then scaling by a factor of 1/sqrt(2)
                         * and then rinse and repeat
                        */
                        c = clockwise.transform(b, null);
                        d=new Point2D.Double(c.getX()-a.getX(),c.getY()-a.getY());
                        c=new Point2D.Double(a.getX()+SCALE_FACTOR*d.getX(),a.getY()+SCALE_FACTOR*d.getY());
                        line.setLine(a, c);
                        gr.draw(line);

                            it.previous();
                            it.add(c);

                        line.setLine(b, c);
                        gr.draw(line);


                    }
                }


                frame.repaint();
            }



        }



    }
}
