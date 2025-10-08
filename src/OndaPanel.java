import java.awt.*;
import javax.swing.*;

public class OndaPanel extends JPanel {

    private double frecuencia = 440;
    private double amplitud = 50; // porcentaje
    private String tipoOnda = "Senoidal";

    public OndaPanel() {
        // No initComponents() necesario
    }

    public void setParametros(double frecuencia, double amplitud, String tipoOnda) {
        this.frecuencia = frecuencia;
        this.amplitud = amplitud;
        this.tipoOnda = tipoOnda;
        repaint(); // Redibuja el panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.GREEN);

        for (int x = 0; x < w; x++) {
            double t = x / (double) w * 2 * Math.PI * 2; // escala horizontal
            int y = h / 2;

            switch (tipoOnda) {
                case "Senoidal":
                    y = (int)(h / 2 - Math.sin(t * frecuencia / 200) * amplitud / 100 * h / 2);
                    break;
                case "Cuadrada":
                    y = (int)(h / 2 - (Math.sin(t * frecuencia / 200) >= 0 ? 1 : -1) * amplitud / 100 * h / 2);
                    break;
                case "Triangular":
                    y = (int)(h / 2 - (2 / Math.PI * Math.asin(Math.sin(t * frecuencia / 200))) * amplitud / 100 * h / 2);
                    break;
            }

            g.drawLine(x, y, x, y); // dibuja el punto
        }
    }
}
