package hlaaftana.wobby.ui

import groovy.transform.CompileStatic
import hlaaftana.wobby.level.ActiveLevel

import javax.swing.*
import java.awt.*

@CompileStatic
class LevelPlayFrame extends JFrame {
	LevelPlayFrame(ActiveLevel l, int width, int height){
		setSize(width, height)
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		LevelPlayPanel panel = new LevelPlayPanel(this, l)
		add(panel)
	}
}

@CompileStatic
class LevelPlayPanel extends JPanel {
	JFrame frame
	ActiveLevel level
	int canvasX = 0
	int canvasY = 0
	boolean inited
	Thread tickingThread

	LevelPlayPanel(JFrame f, ActiveLevel l){
		level = l
		frame = f
		focusable = true
	}

	def initialize(){
		level.initialize(this)
		inited = true
		tickingThread = Thread.startDaemon {
			while (!Thread.interrupted()) {
				repaint()
				sleep 16
			}
		}
	}

	def tick(Graphics2D g){
		level.tick()
		for (it in level.things)
			g.drawImage(it.thing.getTexture(it), canvasX + it.x, canvasY + it.y,
					it.thing.getWidth(it), it.thing.getHeight(it), null)
	}

	@Override
	void paintComponent(Graphics g){
		super.paintComponent(g)
		if (!inited) initialize()
		tick((Graphics2D) g)
	}
}