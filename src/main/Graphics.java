package main;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.eclipse.swt.*;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
public class Graphics {
	Display display;
	Shell shell;
	Group buttons,keys;
	Label[] wKey,bKey;
	Label[][] pads;
	Label logo,load,songs,help,exit,lvol,ltemp;
	static Label info;
	Slider volume;
	List list;
	Spinner tempo;
	String[] wKeyNote = {"С4\n(Z)","D4\n(X)","E4\n(C)","F4\n(V)","G4\n(B)","A4\n(N)","B4\n(M)","С5\n(,|Q)","D5\n(.|W)","E5\n(E)","F5\n(R)","G5\n(T)","A5\n(Y)","B5\n(U)","C6\n(I)"};
	String[] bKeyNote = {"С#4\n(S)","D#4\n(D)","F#4\n(G)","G#4\n(H)","A#4\n(J)","C#5\n(2)","D#5\n(3)","F#5\n(5)","G#5\n(6)","A#5\n(7)"};
	String[] instr = {"Piano","8bit","Guitar","Synth","Bass"};
	String songstr[][] = {
						{"The Legend Of Zelda OST - Lost Woods","vnm vnm vnmew mqmbc xcbc","1","70"},
						{"Маленькая звездочка","qq tt yy t rr ee ww q tt rr ee w tt rr ee w qq tt yy t rr ee ww q ","0","80"},
						{"Neon Genesis Evangelion OP - A Cruel Angel's Thesis","q3r3 rrr76 trt t7ir3 77t77i","0","120"},
						{"Kekkai sensen OP - Hello, World!", "dvj dd vv dvj dd bvbd dddd vbhb dd dbvd bhb dd dvj dd vv dvj dd bvb jbj, bj, b3wjbj,", "0", "200"}
						};
	String path= "sounds/Piano.wav";
	static String infoString="";
	String[][] filters = {{"Файл WAV (*.wav)" , "*.wav"}};
	boolean[] keyPressed;
	double factor = 0;
	public Graphics() {
		draw();
	}
	public void draw()  {
		display = new Display();
		shell = new Shell(display, SWT.NONE|SWT.TITLE);
		shell.setBounds(100, 200, 1000, 620);
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		keys = new Group(shell,SWT.SHADOW_NONE);
		keys.setBounds(-1,294,1000,304);
		keys.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		int x = 88;
		bKey = new Label [10];
		for (int i = 0;i<10;i++) {
			bKey[i] = new Label(keys,SWT.NONE);
			bKey[i].setBounds(x,48,32,160);
			bKey[i].setText(bKeyNote[i]);
			bKey[i].setAlignment(SWT.CENTER);
			bKey[i].setForeground(display.getSystemColor(SWT.COLOR_WHITE));
			bKey[i].setBackground(display.getSystemColor(SWT.COLOR_BLACK));
			if (i == 1||i == 4||i == 6||i == 9) x+=112;
			else x+=64;
			list(bKey[i],bKey[i].getBackground(),(16+i));
		}
		x = 52;
		wKey = new Label[15];
		for(int i = 0;i<15;i++) {
			wKey[i] = new Label(keys,SWT.NONE);
			wKey[i].setBounds(x,48,57,256);
			wKey[i].setText("\n\n\n\n\n\n\n\n\n\n\n\n\n"+wKeyNote[i]);
			wKey[i].setAlignment(SWT.CENTER);
			wKey[i].setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			x+=59;
			list(wKey[i],wKey[i].getBackground(),(1+i));
		}
		keyPressed = new boolean[35];
		for (int i=0;i<keyPressed.length;i++) {
			keyPressed[i]=false;
		}
		buttons = new Group(shell,SWT.SHADOW_ETCHED_IN);
		buttons.setBounds(-1,-10,1000,308);
		buttons.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		logo = new Label(buttons,SWT.NONE|SWT.NO_FOCUS);
		logo.setBounds(52,45,208,52);
		logo.setImage(new Image(display, "logo.jpg"));
		load = new Label(buttons,SWT.NONE&SWT.NO_FOCUS);
		load.setBounds(52,115,80,35);
		load.setImage(new Image(display, "load.jpg"));
		load.setCursor(new Cursor(display,SWT.CURSOR_HAND));
		load.setToolTipText("Загрузить звук");
		load.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) {
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				setFilters(dlg);
				String file=dlg.open();
				if (file!=null) {
					list.add(file);
				}
			}
			@Override
			public void mouseUp(MouseEvent e) {
			}			
		});
		songs = new Label(buttons,SWT.NONE);
		songs.setBounds(180,115,80,35);
		songs.setImage(new Image(display, "songs.jpg"));
		songs.setCursor(new Cursor(display,SWT.CURSOR_HAND));
		songs.setToolTipText("Демо-мелодии");
		songs.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) {
				Shell songwindow = new Shell(display,SWT.NONE|SWT.TITLE);
				songwindow.setText("Выбор мелодии");
				songwindow.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				songwindow.setBounds(shell.getBounds().x,shell.getBounds().y,shell.getBounds().width/3,shell.getBounds().height/3);
				songwindow.setLayout(new GridLayout(2,true));
				List songlist = new List(songwindow,SWT.SINGLE|SWT.V_SCROLL);
				songlist.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,2,1));
				for (int i = 0;i<songstr.length;i++) {
					songlist.add(songstr[i][0]);
				}
				Button ok = new Button(songwindow,SWT.DEFAULT);
				ok.setLayoutData(new GridData(SWT.LEFT,SWT.END,true,true,1,1));
				ok.setText("Выбрать мелодию");
				ok.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (songlist.getSelectionIndex()!=-1) {
							infoString =songstr[songlist.getSelectionIndex()][1];
							list.setSelection(Integer.parseInt(songstr[songlist.getSelectionIndex()][2]));
							path = "sounds/"+list.getItem((Integer.parseInt(songstr[songlist.getSelectionIndex()][2])))+".wav";
							tempo.setSelection(Integer.parseInt(songstr[songlist.getSelectionIndex()][3]));
							info.setText(infoString);
							songwindow.close();
						}
						else {
							MessageBox erbox = new MessageBox(songwindow,SWT.ICON_ERROR|SWT.OK);
							erbox.setText("Ошибка");
							erbox.setMessage("Вы не выбрали мелодию!");
							erbox.open();
						}
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				Button cancel = new Button(songwindow,SWT.DEFAULT);
				cancel.setLayoutData(new GridData(SWT.RIGHT,SWT.END,true,true,1,1));
				cancel.setText("Отмена");
				cancel.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						songwindow.close();
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				songwindow.open();
			}
			@Override
			public void mouseUp(MouseEvent e) {}
		});
		help = new Label(buttons,SWT.NONE&SWT.NO_FOCUS);
		help.setBounds(52,168,80,35);
		help.setImage(new Image(display, "help.jpg"));
		help.setCursor(new Cursor(display,SWT.CURSOR_HAND));
		help.setToolTipText("Помощь");
		help.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
			@Override
			public void mouseDown(MouseEvent e) {
				Shell helpwindow = new Shell(display,SWT.NONE|SWT.TITLE);
				helpwindow.setText("Помощь");
				helpwindow.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				helpwindow.setBounds(shell.getBounds().x,shell.getBounds().y,shell.getBounds().width/3,shell.getBounds().height/3);
				helpwindow.setLayout(new GridLayout(1,true));
				Text help = new Text(helpwindow,SWT.V_SCROLL|SWT.READ_ONLY|SWT.WRAP|SWT.CENTER);
				help.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,1,1));;
				help.setText("Помощь по программе.\n Для игры с клавиатуры, нажимайте на кнопки, указанные в скобках на клавишах, предварительно переключившись на"
						+ " английскую раскладку. Играть на дрампаде можно также с помощью NUM-клавиатуры (со включенным Num Lock). В приложении есть демо-обучающий"
						+ " режим.  (кнопка SONGS). Там вы можете выбрать мелодию и над клавиатурой пианино отобразится порядок кнопок, которые вы должны нажать на клавиатуре."
						+ " С помощью кнопки \"Открыть\" вы можеть загрузить свой звук в формате WAV, длительностью не более 3-х секунд, после чего он отобразится "
						+ "в списке инструментов.");
				help.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				Button ok = new Button(helpwindow,SWT.DEFAULT);
				ok.setLayoutData(new GridData(SWT.CENTER,SWT.END,true,false,1,1));
				ok.setText("Все понятно");
				ok.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						helpwindow.close();
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}					
				});
				helpwindow.open();
			}
			@Override
			public void mouseUp(MouseEvent e) {}
		});
		exit = new Label(buttons,SWT.NONE&SWT.NO_FOCUS);
		exit.setBounds(180,168,80,35);
		exit.setImage(new Image(display, "exit.jpg"));
		exit.setCursor(new Cursor(display,SWT.CURSOR_HAND));
		exit.setToolTipText("Выйти из программы");
		exit.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) {
				shell.close();
			}
			@Override
			public void mouseUp(MouseEvent e) {}
		});
		volume = new Slider(buttons,SWT.HORIZONTAL|SWT.NO_FOCUS);
		volume.setBounds(52,221,208,35);
		volume.setValues(100, 0, 101, 1, 1, 10);
		volume.setCursor(new Cursor(display,SWT.CURSOR_HAND));
		volume.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lvol.setText("Громкость: "+volume.getSelection());
				shell.setFocus();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}	
		});
		lvol = new Label(buttons,SWT.NONE);
		lvol.setBounds(52,256,208,35);
		lvol.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		lvol.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		lvol.setAlignment(SWT.CENTER);
		lvol.setFont(new Font(display, "Calibri", 20, SWT.BOLD));
		lvol.setText("Громкость: 100");
		list = new List(buttons,SWT.SINGLE|SWT.NO_FOCUS|SWT.V_SCROLL);
		list.setBounds(308,115,300,141);
		for (int i = 0;i<instr.length;i++) {
			list.add(instr[i]);
		}
		list.setSelection(0);
		list.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		list.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		list.setCursor(new Cursor(display,SWT.CURSOR_HAND));
		list.setToolTipText("Двойной щелчок удаляет ваш файл из списка");
		list.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((list.getSelectionIndex())<instr.length)
				path = ("sounds/"+list.getItem(list.getSelectionIndex())+".wav");
				else
				path = list.getItem(list.getSelectionIndex());
				shell.setFocus();
				list.addMouseListener(new MouseListener() {
					@Override
					public void mouseDoubleClick(MouseEvent e) {
						if ((list.getSelectionIndex())>=4) {
						list.remove(list.getSelectionIndex());
						list.setSelection(0);}
					}
					@Override
					public void mouseDown(MouseEvent e) {	}
					@Override
					public void mouseUp(MouseEvent e) {}			
				});
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				shell.setFocus();
			}
		});
		ltemp = new Label(buttons,SWT.NONE);
		ltemp.setBounds(308,60,150,47);
		ltemp.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		ltemp.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		ltemp.setAlignment(SWT.RIGHT);
		ltemp.setFont(new Font(display, "Calibri", 20, SWT.BOLD));
		ltemp.setText("Темп (%): ");
		tempo = new Spinner(buttons,SWT.READ_ONLY|SWT.NO_FOCUS);
		tempo.setBounds(458,60,100,40);
		tempo.setFont(new Font(display, "Calibri", 20, SWT.BOLD));
		tempo.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		tempo.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		tempo.setValues(100, 50, 200, 0, 10, 10);
		tempo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setFocus();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		info = new Label(buttons,SWT.NONE);
		info.setBounds(308,265,300,40);
		info.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		info.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		info.setAlignment(SWT.CENTER);
		info.setFont(new Font(display, "Calibri", 12, SWT.NORMAL));
		pads = new Label[3][3];
		int xx = 660;
		int yy = 60;
		int pad = 7;
		for (int i = 0;i < 3; i++) {
			for(int j = 0;j < 3;j++) {
				pads[i][j] = new Label(buttons,SWT.NONE);
				pads[i][j].setBounds(xx,yy,88,70);
				pads[i][j].setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				pList(pads[i][j],pad);
				pad++;
				xx+=93;
			}
			if (i == 0) pad = 4;
			else pad = 1;
			xx=660;
			yy+=75;
		}
		shell.forceFocus();
		shell.open();
		waitClose();
	}
	public void pList(Label label,int pad) {
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					try {
						Sound.startPad(pad);
					} catch (LineUnavailableException e1) {
						e1.printStackTrace();
					}
				} catch (UnsupportedAudioFileException | IOException e1) {
					e1.printStackTrace();
				}
				label.setBackground(display.getSystemColor(SWT.COLOR_RED));
			}
			@Override
			public void mouseUp(MouseEvent e) {
				label.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
			}
		});
	}
	public void list(Label label,Color color,int note) {
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
			@Override
			public void mouseDown(MouseEvent arg0) {
				label.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
				playNote(note);
			}
			@Override
			public void mouseUp(MouseEvent arg0) {
				label.setBackground(color);
			}
		});
		shell.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					switch (e.keyCode) {
					case(SWT.KEYPAD_1):
						if(keyPressed[26]==false) {Sound.startPad(1);keyPressed[26]=true;pads[2][0].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_2):
						if(keyPressed[27]==false) {Sound.startPad(2);keyPressed[27]=true;pads[2][1].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_3):
						if(keyPressed[28]==false) {Sound.startPad(3);keyPressed[28]=true;pads[2][2].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_4):
						if(keyPressed[29]==false) {Sound.startPad(4);keyPressed[29]=true;pads[1][0].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_5):
						if(keyPressed[30]==false) {Sound.startPad(5);keyPressed[30]=true;pads[1][1].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_6):
						if(keyPressed[31]==false) {Sound.startPad(6);keyPressed[31]=true;pads[1][2].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_7):
						if(keyPressed[32]==false) {Sound.startPad(7);keyPressed[32]=true;pads[0][0].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_8):
						if(keyPressed[33]==false) {Sound.startPad(8);keyPressed[33]=true;pads[0][1].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					case(SWT.KEYPAD_9):
						if(keyPressed[34]==false) {Sound.startPad(9);keyPressed[34]=true;pads[0][2].setBackground(display.getSystemColor(SWT.COLOR_RED));}break;
					}
					} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e1) {
						e1.printStackTrace();
					}
				switch(e.character){
					case('z'):
						playNote(1);keyPressed[0]=true;wKey[0].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('x'):
						playNote(2);keyPressed[1]=true;wKey[1].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('c'):
						playNote(3);keyPressed[2]=true;wKey[2].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('v'):
						playNote(4);keyPressed[3]=true;wKey[3].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('b'):
						playNote(5);keyPressed[4]=true;wKey[4].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('n'):
						playNote(6);keyPressed[5]=true;wKey[5].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('m'):
						playNote(7);keyPressed[6]=true;wKey[6].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case(','):
						playNote(8);keyPressed[7]=true;wKey[7].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('q'):
						playNote(8);keyPressed[7]=true;wKey[7].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('.'):
						playNote(9);keyPressed[8]=true;wKey[8].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('w'):
						playNote(9);keyPressed[8]=true;wKey[8].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('e'):
						playNote(10);keyPressed[9]=true;wKey[9].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('r'):
						playNote(11);keyPressed[10]=true;wKey[10].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('t'):
						playNote(12);keyPressed[11]=true;wKey[11].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('y'):
						playNote(13);keyPressed[12]=true;wKey[12].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('u'):
						playNote(14);keyPressed[13]=true;wKey[13].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('i'):
						playNote(15);keyPressed[14]=true;wKey[14].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('s'):
						playNote(16);keyPressed[15]=true;bKey[0].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('d'):
						playNote(17);keyPressed[16]=true;bKey[1].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('g'):
						playNote(18);keyPressed[17]=true;bKey[2].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('h'):
						playNote(19);keyPressed[18]=true;bKey[3].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
					case('j'):
						playNote(20);keyPressed[19]=true;bKey[4].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
				}
				switch(e.keyCode) {
				case(50):
					playNote(21);keyPressed[20]=true;bKey[5].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
				case(51):
					playNote(22);keyPressed[21]=true;bKey[6].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
				case(53):
					playNote(23);keyPressed[22]=true;bKey[7].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
				case(54):
					playNote(24);keyPressed[23]=true;bKey[8].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
				case(55):
					playNote(25);keyPressed[24]=true;bKey[9].setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));break;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				for (int i =0;i<wKey.length;i++) {
					wKey[i].setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				}
				for (int i =0;i<bKey.length;i++) {
					bKey[i].setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				}
				for (int i =0;i<3;i++) 
					for (int j =0;j<3;j++) {
						pads[i][j].setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				}
				for (int i =0;i<keyPressed.length;i++) {
					keyPressed[i]=false;
				}
			}
		});
	}
	void setFilters(FileDialog dialog)
	{
	    String[] names = new String[filters.length];
	    String[] exts  = new String[filters.length];
	    for (int i = 0; i < filters.length; i++) {
	        names[i] = filters[i][0];
	        exts [i] = filters[i][1];
	    }
	    dialog.setFilterNames(names);
	    dialog.setFilterExtensions(exts);
	}
	void playNote(int note) {
		if (keyPressed[note-1]==false) {
		switch (note){
			case (1):
				factor = 2; break;
			case (2):
				factor = 1.8; break;
			case (3):
				factor = 1.6; break;
			case (4):
				factor = 1.5; break;
			case (5):
				factor = 1.35; break;
			case (6):
				factor = 1.2; break;
			case (7):
				factor = 1.06; break;
			case (8):
				factor = 1; break;
			case (9):
				factor = 0.9; break;
			case (10):
				factor = 0.8; break;
			case (11):
				factor = 0.75; break;
			case (12):
				factor = 0.67; break;
			case (13):
				factor = 0.60; break;
			case (14):
				factor = 0.53; break;
			case (15):
				factor = 0.5; break;
			case (16):
				factor = 1.9; break;
			case (17):
				factor = 1.7; break;
			case (18):
				factor = 1.43; break;
			case (19):
				factor = 1.27; break;
			case (20):
				factor = 1.13; break;
			case (21):
				factor = 0.95; break;
			case (22):
				factor = 0.85; break;
			case (23):
				factor = 0.71; break;
			case (24):
				factor = 0.63; break;
			case (25):
				factor = 0.56; break;
			default:
				factor = 2; break;
		}
		try {
			double vol = (double)volume.getSelection()/100;
			double temp = (double)tempo.getSelection()/100;
			setInfo(infoString);
			Sound.startCli(path,factor,vol,temp);
			
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}}
		shell.setFocus();
	}
	static void setInfo(String string) {
		info.setText(string);
	}
	void waitClose(){
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) display.sleep();}
	}
}