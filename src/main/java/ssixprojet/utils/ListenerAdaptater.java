package ssixprojet.utils;

import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface ListenerAdaptater extends MouseListener, MouseMotionListener, KeyListener, WindowListener {

	@Override
	default void keyPressed(KeyEvent e) {}

	@Override
	default void keyReleased(KeyEvent e) {}

	@Override
	default void keyTyped(KeyEvent e) {}

	@Override
	default void mouseClicked(MouseEvent e) {}

	@Override
	default void mouseDragged(MouseEvent e) {}

	@Override
	default void mouseEntered(MouseEvent e) {}

	@Override
	default void mouseExited(MouseEvent e) {}

	@Override
	default void mouseMoved(MouseEvent e) {}

	@Override
	default void mousePressed(MouseEvent e) {}

	@Override
	default void mouseReleased(MouseEvent e) {}

	@Override
	default void windowActivated(WindowEvent e) {}

	@Override
	default void windowClosed(WindowEvent e) {}

	@Override
	default void windowClosing(WindowEvent e) {}

	@Override
	default void windowDeactivated(WindowEvent e) {}

	@Override
	default void windowDeiconified(WindowEvent e) {}

	@Override
	default void windowIconified(WindowEvent e) {}

	@Override
	default void windowOpened(WindowEvent e) {}
}
