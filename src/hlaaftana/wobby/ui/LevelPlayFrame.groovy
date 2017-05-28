package hlaaftana.wobby.ui

import hlaaftana.wobby.level.ActiveLevel
import hlaaftana.wobby.level.ActiveThing

import javax.swing.*
import java.awt.*

class LevelPlayFrame extends JFrame {
	LevelPlayFrame(ActiveLevel l, int width, int height){
		setSize(width, height)
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		LevelPlayPanel panel = new LevelPlayPanel(this, l)
		add(panel)
	}
}

class LevelPlayPanel extends JPanel {
	JFrame frame
	ActiveLevel level
	int canvasX = 0
	int canvasY = 0
	boolean inited

	LevelPlayPanel(JFrame f, ActiveLevel l){
		level = l
		frame = f
		focusable = true
	}

	def initialize(){
		level.initialize(this)
		inited = true
		Thread.startDaemon {
			while (!Thread.interrupted()){
				repaint()
				sleep 20
			}
		}
	}

	def tick(Graphics2D g){
		level.tick()
		level.things.each { ActiveThing it ->
			g.drawImage(it.thing.getTexture(it), canvasX + it.x, canvasY + it.y, null)
		}
	}

	@Override
	void paintComponent(Graphics g){
		super.paintComponent(g)
		if (!inited) initialize()
		tick((Graphics2D) g)
	}
}