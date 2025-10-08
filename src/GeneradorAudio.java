import javax.sound.sampled.*;

public class GeneradorAudio {

   
    private volatile boolean reproduciendo = false;
    private volatile double frecuencia = 440;
    private volatile double amplitud = 50; // 0-100%
    private volatile String tipoOnda = "Senoidal";
    private Thread hiloSonido;

    private final int SAMPLE_RATE = 44100; // Hz

    public void iniciar() {
        detener();
        reproduciendo = true;

        hiloSonido = new Thread(() -> {
            try {
                AudioFormat formato = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
                SourceDataLine linea = AudioSystem.getSourceDataLine(formato);
                linea.open(formato);
                linea.start();

                byte[] buffer = new byte[4410 * 2]; // 16 bits -> 2 bytes por sample
                double fase = 0.0;

                while (reproduciendo) {
                    for (int i = 0; i < buffer.length; i += 2) {
                        double valor = 0.0;

                        switch (tipoOnda) {
                            case "Senoidal":
                                valor = Math.sin(fase);
                                break;
                            case "Cuadrada":
                                valor = Math.signum(Math.sin(fase));
                                break;
                            case "Triangular":
                                valor = 2 * Math.abs(2 * (fase / (2 * Math.PI) - Math.floor(fase / (2 * Math.PI) + 0.5))) - 1;
                                break;
                        }

                        // Escalamos a 16 bits
                        short sample = (short) (valor * Short.MAX_VALUE * (amplitud / 100.0));

                        // Big-endian: MSB primero
                        buffer[i] = (byte) ((sample >> 8) & 0xFF);
                        buffer[i + 1] = (byte) (sample & 0xFF);

                        fase += 2 * Math.PI * frecuencia / SAMPLE_RATE;
                        if (fase > 2 * Math.PI) fase -= 2 * Math.PI;
                    }
                    linea.write(buffer, 0, buffer.length);
                }

                linea.drain();
                linea.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        hiloSonido.start();
    }

    public void detener() {
        reproduciendo = false;
        if (hiloSonido != null && hiloSonido.isAlive()) {
            try {
                hiloSonido.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Control en tiempo real
    public void setFrecuencia(double f) { this.frecuencia = f; }
    public void setAmplitud(double a) { this.amplitud = a; }
    public void setTipoOnda(String tipo) { this.tipoOnda = tipo; }
    public boolean isReproduciendo() { return reproduciendo; }
}