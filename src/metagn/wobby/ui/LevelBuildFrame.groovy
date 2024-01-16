package metagn.wobby.ui

import groovy.transform.CompileStatic
import metagn.wobby.GameData
import metagn.wobby.level.InactiveLevel
import metagn.wobby.level.InactiveThing
import metagn.wobby.things.Thing

import javax.swing.*
import java.awt.*
import java.awt.event.*

@CompileStatic
class LevelBuildFrame extends JFrame {
	LevelBuildFrame(InactiveLevel l, int width, int height) {
		setSize(width, height)
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		LevelBuildPanel panel = new LevelBuildPanel(this, l)
		setJMenuBar(panel.menuBar)
		add(panel)
	}
}

@CompileStatic
class LevelBuildPanel extends JPanel {
	LinkedList<InactiveThing> placedThings = new LinkedList<>()
	JMenuBar menuBar
	JMenu editMenu, buildMenu
	JFileChooser loadFile
	JFrame frame
	InactiveLevel level
	Thing selectedThing
	int gridX = 0, gridY = 0
	int canvasX = 0, canvasY = 0

	LevelBuildPanel(JFrame f, InactiveLevel l) {
		level = l
		frame = f
		focusable = true

		menuBar = new JMenuBar()
		editMenu = new JMenu('Edit')

		def mi = new JMenuItem('Undo')
		mi.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, KeyEvent.VK_Z)
		mi.action = new AbstractAction() {
			void actionPerformed(ActionEvent e) {
				if (placedThings) level.remove(placedThings.pop())
				repaint()
			}
		}
		mi.text = 'Undo'
		editMenu.add(mi)

		mi = new JMenuItem('Load')
		loadFile = new JFileChooser()

		mi.action = new AbstractAction() {
			void actionPerformed(ActionEvent e) {
				loadFile.showOpenDialog(null)
				level = InactiveLevel.decode(loadFile.selectedFile.bytes)
				repaint()
			}
		}

		mi.text = 'Load'
		editMenu.add(mi)

		mi = new JMenuItem('Save')
		loadFile = new JFileChooser()

		mi.action = new AbstractAction() {
			void actionPerformed(ActionEvent e) {
				loadFile.showSaveDialog(null)
				loadFile.selectedFile.bytes = level.encode()
			}
		}

		mi.text = 'Save'
		editMenu.add(mi)

		mi = new JMenuItem('Play')

		mi.action = new AbstractAction() {
			void actionPerformed(ActionEvent e) {
				def x = new LevelPlayFrame(level.activate(), frame.width, frame.height)
				x.visible = true
			}
		}

		mi.text = 'Play'
		editMenu.add(mi)

		menuBar.add(editMenu)

		buildMenu = new JMenu('Build')

		mi = new JMenuItem('Set grid')

		mi.action = new AbstractAction() {
			void actionPerformed(ActionEvent e) {
				try {
					gridX = JOptionPane.showInputDialog(LevelBuildPanel.this, 'Enter grid width').toInteger()
					gridY = JOptionPane.showInputDialog(LevelBuildPanel.this, 'Enter grid height').toInteger()
				} catch (ignored) {
					JOptionPane.showMessageDialog(LevelBuildPanel.this, 'Invalid number.')
				}
			}
		}

		mi.text = 'Set grid'
		buildMenu.add(mi)

		mi = new JMenuItem('Select thing')

		mi.action = new AbstractAction() {
			@Override
			void actionPerformed(ActionEvent e) {
				def t = GameData.thing(JOptionPane.showInputDialog(LevelBuildPanel.this, 'Enter thing identifier'))
				if (t) selectedThing = t
				else JOptionPane.showMessageDialog(LevelBuildPanel.this, 'Invalid thing.')
			}
		}

		mi.text = 'Select thing'
		buildMenu.add(mi)

		mi = new JMenuItem('Clear')

		mi.action = new AbstractAction() {
			@Override
			void actionPerformed(ActionEvent e) {
				level.placements.clear()
				level.all.clear()
				repaint()
			}
		}

		mi.text = 'Clear'
		buildMenu.add(mi)

		menuBar.add(buildMenu)

		addMouseListener new MouseAdapter() {
			void mousePressed(MouseEvent e) {
				if (e.button == MouseEvent.BUTTON1) {
					if (e.clickCount == 2)
						selectedThing = level.thingsIn(e.x - canvasX, e.y - canvasY)[0]?.thing
					else if (selectedThing) {
						int x = e.x - canvasX
						int y = e.y - canvasY
						if (gridX && gridY) {
							x -= x % gridX
							y -= y % gridY
						}
						placedThings << level.place(x, y, selectedThing)
						repaint()
					}
				} else if (e.button == MouseEvent.BUTTON2)
					selectedThing = level.thingsIn(e.x - canvasX, e.y - canvasY)[0]?.thing
				else if (e.button == MouseEvent.BUTTON3) {
					level.removeTopIn(e.x - canvasX, e.y - canvasY)
					repaint()
				}
			}
		}

		addKeyListener new KeyAdapter() {
			@Override
			void keyPressed(KeyEvent e) {
				if (e.keyCode == KeyEvent.VK_RIGHT)
					canvasX = Math.max(canvasX - 10, Math.min(frame.width - level.maxX, 0))
				else if (e.keyCode == KeyEvent.VK_LEFT)
					canvasX = Math.min(canvasX + 10, 0)
				else if (e.keyCode == KeyEvent.VK_DOWN)
					canvasY = Math.max(canvasY - 10, Math.min(frame.height - level.maxY, 0))
				else if (e.keyCode == KeyEvent.VK_UP)
					canvasY = Math.min(canvasY + 10, 0)
				repaint()
			}
		}
	}

	@Override
	void paintComponent(Graphics g) {
		super.paintComponent(g)
		Graphics2D g2 = (Graphics2D) g
		for (it in level.all)
			g2.drawImage(it.thing.getTexture(it), canvasX + it.x, canvasY + it.y, null)
	}
}
