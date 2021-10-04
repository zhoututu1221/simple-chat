package com.joker.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ChatClient {

	public static String linkIp = "127.0.0.1";
	public static Integer linkPort = 1234;
	public static StringBuffer c = new StringBuffer();

//	打开文件夹
	public static void openFolder() {
		File file = new File("D://SimpleChat");
		if (!file.exists()) {
			file.mkdir();
		}
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	获取文件名
	public static List<String> getFiles(String path) {
		List<String> files = new ArrayList<String>();
		File file = new File(path);
		File[] tempList = file.listFiles();

		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				files.add(tempList[i].getName());
			}
			if (tempList[i].isDirectory()) {

			}
		}
		return files;
	}

//	显示设置窗口
	public static void showSetting(JFrame f) {
		JDialog d = new JDialog(f);
		d.setModal(true);

		d.setTitle("设置连接信息");
		d.setSize(400, 260);
		d.setLocationRelativeTo(null);
		d.setLayout(null);
		d.setResizable(false);

		JLabel ipLabel = new JLabel();
		ipLabel.setText("IP：");
		ipLabel.setBounds(50, 50, 50, 30);

		JTextField ip = new JTextField();
		ip.setBounds(100, 50, 200, 30);
		ip.setText(linkIp);

		JLabel portLabel = new JLabel();
		portLabel.setText("端口：");
		portLabel.setBounds(50, 100, 50, 30);

		JTextField port = new JTextField();
		port.setBounds(100, 100, 200, 30);
		port.setText(linkPort.toString());

		JButton ok = new JButton();
		ok.setText("确定");
		ok.setBounds(100, 150, 200, 30);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				linkIp = ip.getText();
				linkPort = Integer.parseInt(port.getText());
			}
		});

		d.add(ipLabel);
		d.add(ip);
		d.add(portLabel);
		d.add(port);
		d.add(ok);
		d.setVisible(true);
	}

	public static void runChat() {

//		初始化文件夹
		File file = new File("D://SimpleChat");
		if (!file.exists()) {
			file.mkdir();
		}
		File file1 = new File("D://SimpleChat/backup");
		if (!file1.exists()) {
			file1.mkdir();
		}

		JFrame f = new JFrame("SimpleChat-简易聊天");
		f.setSize(800, 600);
		f.setLocationRelativeTo(null);
		f.setLayout(new BorderLayout());

//		聊天框
		JEditorPane content = new JEditorPane();
		content.setContentType("text/html;charset=utf-8");
		content.setText(c.toString());
		content.setAutoscrolls(true);
		JScrollPane jScrollPane = new JScrollPane(content);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

//		打开文件夹和获取文件夹内文件名称
		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(f.getWidth() / 5, f.getHeight()));
		left.setLayout(new BorderLayout());
		JButton openF = new JButton();
		openF.setText("打开传输文件夹");
		openF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openFolder();
			}
		});
		JPanel leftList = new JPanel();
		leftList.setLayout(new BorderLayout());
		leftList.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		leftList.setBackground(Color.white);
		JTextArea fileList = new JTextArea();
		fileList.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 20));
		new Thread() {
			public void run() {
				StringBuffer s = new StringBuffer();
				while (true) {
					s.setLength(0);
					List<String> list = getFiles("D://SimpleChat");
					for (int i = 0; i < list.size(); i++) {
						s.append(list.get(i) + "\n");
					}
					fileList.setText(s.toString());
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		JLabel leftTitle = new JLabel("文件列表：");
		leftTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 20));
		leftList.add(leftTitle, BorderLayout.NORTH);
		leftList.add(fileList, BorderLayout.CENTER);

		left.add(openF, BorderLayout.NORTH);
		left.add(leftList, BorderLayout.CENTER);

//		设置连接信息
		JPanel right = new JPanel();
		right.setBackground(Color.white);
		right.setPreferredSize(new Dimension(f.getWidth() / 5, f.getHeight()));
		right.setLayout(null);
		right.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
		JLabel title = new JLabel();
		title.setText("客户端");
		title.setBounds(0, 0, f.getWidth() / 5, 30);
		title.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel ip = new JLabel();
		ip.setBounds(5, 40, f.getWidth() / 5 - 10, 30);

		JLabel port = new JLabel();
		port.setBounds(5, 80, f.getWidth() / 5 - 10, 30);

		new Thread() {
			public void run() {
				while (true) {
					ip.setText("连接IP:" + linkIp);
					port.setText("连接端口:" + linkPort);
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

		JButton setting = new JButton("设置");
		setting.setBounds(5, 120, f.getWidth() / 5 - 10, 30);
		setting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSetting(f);
			}
		});

		JButton link = new JButton("连接");
		link.setBounds(5, 160, f.getWidth() / 5 - 10, 30);

		JLabel status = new JLabel();
		status.setText("状态:未连接");
		status.setBounds(5, 200, f.getWidth() / 5 - 10, 30);

		right.add(title);
		right.add(ip);
		right.add(port);
		right.add(setting);
		right.add(link);
		right.add(status);

//		输入内容部分
		JPanel bottom = new JPanel();
		bottom.setLayout(null);
		bottom.setPreferredSize(new Dimension(f.getWidth(), f.getHeight() / 4));

		JTextArea inText = new JTextArea();
		inText.setBounds(0, 0, f.getWidth(), f.getHeight() / 4 - 40);
		inText.setBorder(BorderFactory.createLineBorder(Color.gray));
		inText.setBackground(new Color(243, 243, 245));

		JPanel bottomControl = new JPanel();
		bottomControl.setLayout(new FlowLayout());
		bottomControl.setBounds(0, f.getHeight() / 4 - 40, f.getWidth(), 40);
		bottomControl.setBackground(Color.white);

		JButton selectFile = new JButton();
		selectFile.setText("选择文件");

		JButton send = new JButton();
		send.setText("发送");

		bottomControl.add(selectFile);
		bottomControl.add(send);

		bottom.add(inText);
		bottom.add(bottomControl);

		f.add(jScrollPane, BorderLayout.CENTER);
		f.add(left, BorderLayout.WEST);
		f.add(right, BorderLayout.EAST);
		f.add(bottom, BorderLayout.SOUTH);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

//		实现聊天功能
//		连接
		link.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Socket client;
				try {
					client = new Socket(linkIp, linkPort);
					OutputStream outToServer = client.getOutputStream();
					DataOutputStream out = new DataOutputStream(outToServer);

					InputStream inFromServer = client.getInputStream();
					DataInputStream in = new DataInputStream(inFromServer);

					status.setText("状态：连接成功");
					
//					读取聊天记录
					File log = new File("D:\\SimpleChat\\backup\\"+linkPort+".txt");
					if(log.exists()) {
						BufferedReader bReader = new BufferedReader(new FileReader(log));
						String one = null;
						StringBuffer all = new StringBuffer();
						while ((one = bReader.readLine()) != null) {
							all.append(one);
						}
						content.setText(all.toString());
						c.append(all.toString());
						bReader.close();
					}
					
//					获取消息
					new Thread() {
						public void run() {
							super.run();
							try {
								while (true) {
									
					                String fileName = in.readUTF();
					                
					                String fileNameSplit[] = fileName.split("-");

									if(fileNameSplit[0].equals("1")) {
										c.append("<div style='color: blue;font-size: 16px;width: 90%;'>他：" + fileNameSplit[1] + "</div><br>");
										content.setText(c.toString());
									}else {
										c.append("<div style='color: blue;font-size: 16px;width: 90%;'>他：" + fileNameSplit[1] + "</div><br>");
										content.setText(c.toString());
										long fileLength = in.readLong();
										
										File file = new File("D:\\SimpleChat\\"+fileNameSplit[1]);
										FileOutputStream fos = new FileOutputStream(file);
										 // 开始接收文件
						                byte[] bytes = new byte[1024];
						                int length = 0;
						                while((length = in.read(bytes, 0, bytes.length)) != -1) {
						                    fos.write(bytes, 0, length);
						                    fos.flush();
						                }
						                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + fileLength + "] ========");
									}
									
									
//									保存聊天记录
									writeTxt("D:\\SimpleChat\\backup\\"+linkPort+".txt",c.toString());
									
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();

//					发送消息
					send.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								c.append("<div style='color: green;font-size: 16px;width: 90%;'>我：" + inText.getText()
										+ "</div><br>");
								content.setText(c.toString());
								out.writeUTF("1-"+inText.getText());
								out.flush();
//								保存聊天记录
								writeTxt("D:\\SimpleChat\\backup\\"+linkPort+".txt",c.toString());
//								设置输入框为空
								inText.setText("");
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});

//					发送文件
					selectFile.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							int option = fileChooser.showOpenDialog(f);
							if (option == JFileChooser.APPROVE_OPTION) {
								try {
									File file = fileChooser.getSelectedFile();
									FileInputStream fis = new FileInputStream(file);
									out.writeUTF("2-"+file.getName());
									out.flush();
									out.writeLong(file.length());
									out.flush();
									System.out.println("======== 开始传输文件 ========");
									byte[] bytes = new byte[1024];
									int length = 0;
									long progress = 0;
									while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
										out.write(bytes, 0, length);
										out.flush();
										progress += length;
										System.out.print("| " + (100 * progress / file.length()) + "% |");
									}
									System.out.println();
									System.out.println("======== 文件传输成功 ========");
									c.append("<div style='font-size: 14px;width: 90%;'>我：发送文件-" + file.getName() + "</div><br>");
									content.setText(c.toString());
								} catch (Exception e2) {
									e2.printStackTrace();
								}
//								保存聊天记录
								writeTxt("D:\\SimpleChat\\backup\\"+linkPort+".txt",c.toString());
							} else {
								System.out.println("取消选择文件");
							}
						}
					});

				} catch (UnknownHostException e1) {
					e1.printStackTrace();
					status.setText("状态：连接失败");
				} catch (IOException e1) {
					e1.printStackTrace();
					status.setText("状态：连接失败");
				}
			}
		});

	}

//	写出文件
	public static void writeTxt(String txtPath,String content){    
       FileOutputStream fileOutputStream = null;
       File file = new File(txtPath);
       try {
           if(file.exists()){
               file.createNewFile();
           }
           fileOutputStream = new FileOutputStream(file);
           fileOutputStream.write(content.getBytes());
           fileOutputStream.flush();
           fileOutputStream.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
	
	public static void main(String[] args) {

		new Thread() {
			public void run() {
				runChat();
			};
		}.start();

	}

}
