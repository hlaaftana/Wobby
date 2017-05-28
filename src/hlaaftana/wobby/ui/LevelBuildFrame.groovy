package hlaaftana.wobby.ui

import hlaaftana.wobby.GameData
import hlaaftana.wobby.level.InactiveLevel
import hlaaftana.wobby.level.InactiveThing

import java.awt.*
import java.awt.event.*
import javax.swing.*

import hlaaftana.wobby.things.Thing

class LevelBuildFrame extends JFrame {
	LevelBuildFrame(InactiveLevel l, int width, int height){
		setSize(width, height)
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		LevelBuildPanel panel = new LevelBuildPanel(this, l)
		setJMenuBar(panel.menuBar)
		add(panel)
	}
}

class LevelBuildPanel extends JPanel {
	LinkedList<InactiveThing> placedThings = []
	JMenuBar menuBar
	JMenu editMenu
	JMenu buildMenu
	JFileChooser loadFile
	JFrame frame
	InactiveLevel level
	Thing selectedThing
	int gridX = 0
	int gridY = 0
	int canvasX = 0
	int canvasY = 0

	LevelBuildPanel(JFrame f, InactiveLevel l){
		level = l
		frame = f
		focusable = true
		menuBar = new JMenuBar()
		editMenu = new JMenu('Edit')
		def mi = new JMenuItem('Undo')
		mi.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, KeyEvent.VK_Z)
		mi.action = { if (placedThings) level.remove(placedThings.pop()); repaint() } as AbstractAction
		mi.text = 'Undo'
		editMenu.add(mi)
		mi = new JMenuItem('Load')
		loadFile = new JFileChooser()
		mi.action = {
			loadFile.showOpenDialog(null)
			level = InactiveLevel.decode(loadFile.selectedFile.bytes)
			repaint()
		} as AbstractAction
		mi.text = 'Load'
		editMenu.add(mi)
		mi = new JMenuItem('Save')
		loadFile = new JFileChooser()
		mi.action = {
			loadFile.showSaveDialog(null)
			loadFile.selectedFile.bytes = level.encode()
		} as AbstractAction
		mi.text = 'Save'
		editMenu.add(mi)
		mi = new JMenuItem('Play')
		mi.action = {
			def x = new LevelPlayFrame(level.activate(), frame.width, frame.height)
			x.visible = true
		} as AbstractAction
		mi.text = 'Play'
		editMenu.add(mi)
		menuBar.add(editMenu)
		buildMenu = new JMenu('Build')
		mi = new JMenuItem('Set grid')
		mi.action = {
			try {
				gridX = JOptionPane.showInputDialog(null, 'Enter grid width').toInteger()
				gridY = JOptionPane.showInputDialog(null, 'Enter grid height').toInteger()
			}catch (ex){
				JOptionPane.showMessageDialog(null, 'Invalid number.')
			}
		} as AbstractAction
		mi.text = 'Set grid'
		buildMenu.add(mi)
		mi = new JMenuItem('Select thing')
		mi.action = {
			def t = GameData.thing(JOptionPane.showInputDialog(null, 'Enter thing identifier'))
			if (t){
				selectedThing = t
			}else{
				JOptionPane.showMessageDialog(null, 'Invalid thing.')
			}
		} as AbstractAction
		mi.text = 'Clear'
		buildMenu.add(mi)
		mi = new JMenuItem('Clear')
		mi.action = {
			level.placements.clear()
			level.all.clear()
			repaint()
		} as AbstractAction
		mi.text = 'Select thing'
		buildMenu.add(mi)
		menuBar.add(buildMenu)
		addMouseListener(new MouseAdapter(){
			@Override
			void mousePressed(MouseEvent e){
				if (e.button == MouseEvent.BUTTON1){
					if (e.clickCount == 2)
						selectedThing = level.thingsIn(e.x - canvasX, e.y - canvasY)[0]?.thing
					else if (selectedThing) {
						int x = e.x - canvasX
						int y = e.y - canvasY
						if (gridX && gridY){
							x -= x % gridX
							y -= y % gridY
						}
						placedThings << level.place(x, y, selectedThing)
						repaint()
					}
				}else if (e.button == MouseEvent.BUTTON2)
					selectedThing = level.thingsIn(e.x - canvasX, e.y - canvasY)[0]?.thing
				else if (e.button == MouseEvent.BUTTON3) {
					level.removeTopIn(e.x - canvasX, e.y - canvasY)
					repaint()
				}
			}
		})
		addKeyListener(new KeyAdapter(){
			@Override
			void keyPressed(KeyEvent e){
				if (e.keyCode == KeyEvent.VK_RIGHT)
					canvasX = Math.max(canvasX - 10, Math.min(frame.width - level.maxX, 0))
				if (e.keyCode == KeyEvent.VK_LEFT)
					canvasX = Math.min(canvasX + 10, 0)
				if (e.keyCode == KeyEvent.VK_DOWN)
					canvasY = Math.max(canvasY - 10, Math.min(frame.height - level.maxY, 0))
				if (e.keyCode == KeyEvent.VK_UP)
					canvasY = Math.min(canvasY + 10, 0)
				repaint()
			}
		})
	}

	@Override
	void paintComponent(Graphics g){
		super.paintComponent(g)
		Graphics2D g2 = (Graphics2D) g
		level.all.each { InactiveThing it ->
			g2.drawImage(it.thing.getTexture(it), canvasX + it.x, canvasY + it.y, null)
		}
	}
}