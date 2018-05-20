// Menu - a main menu and some other side menu pages

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends JPanel
{
	private Color backColor;	// used to color the background and all buttons
	private Color textColor;
	private Font buttonFont;	// used for buttons (who knew!)

	// pre - none
	// post - constructs a new Menu
	public Menu()
	{
		buttonFont = new Font("Lucida", Font.ITALIC, 36);
		backColor = new Color(218, 214, 104);
		textColor = new Color(104, 110, 74);
		this.setBackground(backColor);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	// makes buttons stack top to bottom
		
		makeButtonsAndTitle();
	}
	
	// pre - none
	// post - constructs buttons and a title for the menu, adds these to the menu
	private void makeButtonsAndTitle()
	{
		JButton play = makeAButton("NEW GAME!");
		JButton resume = makeAButton("RESUME!");
		if (!WorldRunner.gameActive())
			resume.setEnabled(false);
		JButton settings = makeAButton("SETTINGS!");
		JButton how2play = makeAButton("HOW TO PLAY?!");
		JButton credits = makeAButton("CREDITS!");
		
		play.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WorldRunner.startGame(true);
			}
		});
		resume.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WorldRunner.startGame(false);
			}
		});
		settings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String s = "\t\t\tTHERE ARE NO SETTINGS LOL";
				WorldRunner.goToPage(makeMenuPage(s));
			}
		});
		how2play.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String s = "\t\t\tArrow Keys to move" +
							"\n\t\t\tSpacebar to jump" + 
							"\n\t\t\tC to attack" + 
							"\n\t\t\tESC to return to menu" + 
							"\n\t\t\tWhen you are in front of a shop," + 
							"\n\t\t\t      press 1 to buy health and 2 to buy strength" + 
							"\n\t\t\tRun to the right" + 
							"\n\t\t\tGet $$$" + 
							"\n\n\t\t\tDON'T DIE!";
				WorldRunner.goToPage(makeMenuPage(s));
			}
		});
		credits.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String s = "\t\t\tSanjay \"Swagmaster\" Mohan" + 
							"\n\n\t\t\tAri \"Sp00ked Ya\" Sweedler" + 
							"\n\n\t\t\tAidan \"Best Eyes\" Adams-Campeau";
				WorldRunner.goToPage(makeMenuPage(s));
			}
		});
		
		JLabel title = new JLabel("CONWAY");
		title.setBackground(backColor);
		title.setFont(new Font("TimesRoman", Font.BOLD, 48));
		title.setForeground(textColor);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(Box.createRigidArea(new Dimension(0, 20)));	// spacing!
		this.add(title);
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		this.add(play);
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		this.add(resume);
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		this.add(settings);
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		this.add(how2play);
		this.add(Box.createRigidArea(new Dimension(0, 40)));
		this.add(credits);
	}
	
	// pre - the text to be shown on the button
	// post - returns a new button with a fixed appearance and given text
	private JButton makeAButton(String name)
	{
		JButton button = new JButton(name);
		button.setBackground(backColor);
		button.setForeground(textColor);
		button.setFont(buttonFont);
		button.setBorderPainted(false);	// no more boxes around my words
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFocusable(false);		// removes an annoying highlight box that appears when you click the button
		return button;
	}
	
	// pre - the text to be shown on a menu page
	// post - returns a JPanel comprising a page of the menu
	private JPanel makeMenuPage(String words)
	{
		JPanel page = new JPanel();	// declared final in order to be accessed by the button's actionPerformed
		page.setBackground(backColor);
		page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
		
		JButton back = new JButton("<= Menu");
		back.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WorldRunner.backToMenu();
			}
		});
		back.setBackground(backColor);
		back.setForeground(textColor);
		back.setFont(buttonFont);
		back.setFocusable(false);
		back.setBorderPainted(false);
		back.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JTextArea txt = new JTextArea(words);
		txt.setBackground(backColor);
		txt.setForeground(textColor);
		txt.setEditable(false);
		txt.setFont(new Font("TimesRoman", Font.PLAIN, 28));
		txt.setTabSize(7);
		txt.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		page.add(back);
		page.add(Box.createRigidArea(new Dimension(0, 40)));
		page.add(txt);
		return page;
	}
}