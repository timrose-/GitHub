import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Display extends JPanel {


	int IMAGE_WIDTH, IMAGE_HEIGHT;
	RGB [] [] image;

	Display(int width, int height, RGB [] [] img) {

		this.IMAGE_WIDTH = width;
		this.IMAGE_HEIGHT = height;
		this.image = img;

		setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT)) ;

		JFrame frame = new JFrame("Ray Tracing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.pack();
		frame.setVisible(true);
	}

	public void paintComponent(Graphics g) {
		for(int i = 0 ; i < IMAGE_WIDTH ; i++) {
			for(int j = 0 ; j < IMAGE_HEIGHT ; j++) {
				RGB pixel = image [i] [j] ;
				Color c ;
				if(pixel != null) {
					c = new Color((float) Math.min(1.0, pixel.r),
							(float) Math.min(1.0, pixel.g),
							(float) Math.min(1.0, pixel.b)) ;
				}
				else {
					c = Color.BLACK ;
				}
				g.setColor(c) ;
				g.fillRect(i, j, 1, 1) ;
			}
		}
	}
}