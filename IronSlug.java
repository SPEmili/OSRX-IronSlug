package typewriter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class IronSlug extends JFrame {

    private final JTextPane textPane;
    private final RTFEditorKit rtfKit;
    private String chosenFile = null;
    
    
    //for about and license thing
    About about = new About();
    
    //courtesy of WindowBuilder
    public IronSlug() {
        setTitle("OS/RX IronSlug 0.4.2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPane = new JTextPane();
        rtfKit = new RTFEditorKit();
        textPane.setEditorKit(rtfKit);

        JScrollPane scrollPane = new JScrollPane(textPane);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(createToolBar(), BorderLayout.NORTH);
        setJMenuBar(createMenuBar());
    }

    //also courtesy of WindowBuilder
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        //indentation is broken because of copy-pasting from another file
        Integer[] sizes = new Integer[] {
                new Integer(10), new Integer(12), new Integer(14), new Integer(16),
                new Integer(18), new Integer(20), new Integer(24), new Integer(28),
                new Integer(36), new Integer(48), new Integer(72), new Integer(100)
            };
            final JComboBox sizeBox = new JComboBox(sizes);
            sizeBox.setSelectedItem(new Integer(12));
            sizeBox.setMaximumSize(new Dimension(80, 25));
            sizeBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Integer size = (Integer) sizeBox.getSelectedItem();
                    if (size != null) {
                        new StyledEditorKit.FontSizeAction("fontSize", size.intValue()).actionPerformed(e);
                    }
                }
            });
            
            final JComboBox fontBox = new JComboBox(ISFontManager.getAvailableFontFamilyNames());
            fontBox.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		// Get the selected font family name from fontBox
            		String selectedFont = (String) fontBox.getSelectedItem();

            		if (selectedFont != null) {
            		    // 1. Update rich text attributes for the selected text or caret position
            		    SimpleAttributeSet attr = new SimpleAttributeSet();
            		    StyleConstants.setFontFamily(attr, selectedFont);
            		    
            		    // Replace 'rtfEditor' with your JTextPane variable name
            		    textPane.setCharacterAttributes(attr, false);
            		}
            	}
            });
            fontBox.setMaximumSize(new Dimension(160, 25));
            fontBox.setSelectedItem("Dialog");
            toolBar.add(fontBox);
            toolBar.add(sizeBox);
        
        
        final JButton boldBtn = new JButton(new StyledEditorKit.BoldAction());
        boldBtn.setText("B");
        boldBtn.setFont(boldBtn.getFont().deriveFont(Font.BOLD));

        final JButton italicBtn = new JButton(new StyledEditorKit.ItalicAction());
        italicBtn.setText("i");
        italicBtn.setFont(italicBtn.getFont().deriveFont(italicBtn.getFont().getStyle() | Font.ITALIC));

        final JButton underlineBtn = new JButton(new StyledEditorKit.UnderlineAction());        
        underlineBtn.setFont(underlineBtn.getFont().deriveFont(underlineBtn.getFont().getStyle() | Font.BOLD));
        underlineBtn.setText("U");
        
        
        Component strut1 = Box.createHorizontalStrut(20);
        toolBar.add(strut1);

        toolBar.add(boldBtn);
        toolBar.add(italicBtn);
        toolBar.add(underlineBtn);
        
        Component strut2 = Box.createHorizontalStrut(20);
        toolBar.add(strut2);
        
        JButton btnL = new JButton(new StyledEditorKit.AlignmentAction("Left", StyleConstants.ALIGN_LEFT));
        JButton btnC = new JButton(new StyledEditorKit.AlignmentAction("Center", StyleConstants.ALIGN_CENTER));
        JButton btnR = new JButton(new StyledEditorKit.AlignmentAction("Right", StyleConstants.ALIGN_RIGHT));
        toolBar.add(btnL); toolBar.add(btnC); toolBar.add(btnR);
 
 Component strut3 = Box.createHorizontalStrut(20);
 toolBar.add(strut3);
 
 JButton btnColor = new JButton("Color");
 btnColor.addActionListener(new ActionListener() {
 	public void actionPerformed(ActionEvent e) {
 		Color selectedColor = JColorChooser.showDialog(null, "Select Text Color", Color.BLACK);

 	    if (selectedColor != null) {
 	        SimpleAttributeSet attr = new SimpleAttributeSet();
 	        StyleConstants.setForeground(attr, selectedColor);

 	        // Apply as character attribute to selected text or new text at caret position
 	        textPane.setCharacterAttributes(attr, false);
 	    }
 	}
 });
 toolBar.add(btnColor);
        

        return toolBar;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu aboutMenu = new JMenu("About");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textPane.setText("");
                chosenFile = null;
                setTitle("IronSlug");
            }
        });

        JMenuItem openItem = new JMenuItem("Open RTF...");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        JMenuItem saveItem = new JMenuItem("Save RTF...");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAsFile();
            }
        });
        
        JMenuItem aboutItem = new JMenuItem("About IronSlug");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!about.isRunning)
            		about.main(null);
            }
        });

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        aboutMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        

        return menuBar;
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new RTFFileFilter());
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                textPane.setText("");
                rtfKit.read(in, textPane.getDocument(), 0);
                chosenFile = file.getAbsolutePath();
                setTitle("OS/RX IronSlug - "+file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (in != null) {
                    try { in.close(); } catch (Exception ignored) {}
                }
            }
            
        }
    }

    
    //snippet from another project, to be updated and cleaned up in a later version
    private void saveAsFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new RTFFileFilter());
        if(chosenFile == null)
        {
        	if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".rtf")) {
                    file = new File(file.getAbsolutePath() + ".rtf");
                    chosenFile = file.getAbsolutePath();
                }
            }
        }
        saveFile(new File(chosenFile));
    }
    
    private void saveFile(File file)
    {
    	OutputStream out = null;
    	try {
            out = new FileOutputStream(file);
            rtfKit.write(out, textPane.getDocument(), 0, textPane.getDocument().getLength());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (out != null) {
                try { out.close(); } catch (Exception ignored) {}
            }
        }
    	setTitle("OS/RX IronSlug - "+file.getAbsolutePath());
    }

    //file filter thing for java 3 (to be moved into its own file)
    private static class RTFFileFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            return f.getName().toLowerCase().endsWith(".rtf");
        }

        public String getDescription() {
            return "RTF documents (.rtf)";
        }
    }

    
    //yet another courtesy of WindowBuilder
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
                new IronSlug().setVisible(true);
            }
        });
    }
}