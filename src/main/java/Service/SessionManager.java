package Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class SessionManager {
    private static SessionManager instance;
    private Timer sessionTimer;
    private long lastActivityTime;
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    private boolean sessionActive = true;
    private JFrame parentFrame;
    private Runnable logoutAction;
    
    private SessionManager() {
        lastActivityTime = System.currentTimeMillis();
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void startSession(JFrame parentFrame, Runnable logoutAction) {
        this.parentFrame = parentFrame;
        this.logoutAction = logoutAction;
        sessionActive = true;
        
        // Reset activity timer on any user interaction
        setupGlobalListeners(parentFrame);
        
        // Start session timer
        sessionTimer = new Timer(true);
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkSessionTimeout();
            }
        }, 10000, 10000); // Check every 10 seconds
    }
    
    private void setupGlobalListeners(JFrame frame) {
        // ===== SIMPLER APPROACH: Use AWT Event Queue listener =====
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                // Reset timer on any mouse or keyboard event
                if (event.getID() == MouseEvent.MOUSE_CLICKED ||
                    event.getID() == MouseEvent.MOUSE_PRESSED ||
                    event.getID() == MouseEvent.MOUSE_RELEASED ||
                    event.getID() == MouseEvent.MOUSE_MOVED ||
                    event.getID() == MouseEvent.MOUSE_DRAGGED ||
                    event.getID() == KeyEvent.KEY_PRESSED ||
                    event.getID() == KeyEvent.KEY_RELEASED ||
                    event.getID() == KeyEvent.KEY_TYPED) {
                    resetActivityTimer();
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | 
           AWTEvent.MOUSE_MOTION_EVENT_MASK | 
           AWTEvent.KEY_EVENT_MASK);
        
        // Also add direct listener to frame
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetActivityTimer();
            }
        });
    }
    
    public void resetActivityTimer() {
        lastActivityTime = System.currentTimeMillis();
    }
    
    public long getLastActivityTime() {
        return lastActivityTime;
    }
    
    private void checkSessionTimeout() {
        if (!sessionActive) return;
        
        long currentTime = System.currentTimeMillis();
        long inactiveTime = currentTime - lastActivityTime;
        
        if (inactiveTime > SESSION_TIMEOUT) {
            sessionActive = false;
            SwingUtilities.invokeLater(() -> {
                // Show warning dialog
                int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "⚠️ Your session has been inactive for 30 minutes.\n\n" +
                    "Do you want to continue your session?",
                    "Session Timeout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Reset session
                    resetActivityTimer();
                    sessionActive = true;
                } else {
                    // Logout
                    if (logoutAction != null) {
                        logoutAction.run();
                    }
                    JOptionPane.showMessageDialog(parentFrame,
                        "🔒 You have been logged out due to inactivity.",
                        "Session Ended",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }
    
    public void endSession() {
        sessionActive = false;
        if (sessionTimer != null) {
            sessionTimer.cancel();
            sessionTimer = null;
        }
    }
    
    public boolean isSessionActive() {
        return sessionActive;
    }
}