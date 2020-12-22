package com.fun.tc.nc.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;

public class CreateCAEDialog extends AbstractAIFDialog implements ActionListener {

	private static final long serialVersionUID = -3032849889897906912L;
	
	TCComponentItemRevision processRev;
	
	String type = "MENCMachining";

	private JButton okButton;

	private JButton closeButton;

	private JTextField idText;

	private JTextField revText;

	private JTextField nameText;

	private JButton assignButton;
	
	TCComponentItemType itemType;

	TCSession session;
	
	public CreateCAEDialog(TCComponentItemRevision processRev) throws TCException {
		super(AIFUtility.getActiveDesktop());
		setTitle("新建数控工序（MENCMachining）");
		this.processRev = processRev;
		session = processRev.getSession();
		itemType = (TCComponentItemType) session.getTypeComponent(type);
		String name = processRev.getProperty("object_name");
		JPanel propertyPanel = new JPanel();
		propertyPanel.setLayout(new PropertyLayout());
		propertyPanel.add("1.1.center.center", new JLabel("ID/版本.名称："));
		JPanel createPanel = new JPanel();
		idText = new JTextField(10);
		revText = new JTextField(2);
		nameText = new JTextField(12);
		nameText.setText(name + "数控程序集");
		nameText.setEnabled(false);
		assignButton = new JButton("指派");
		assignButton.addActionListener(this);
		createPanel.add(idText);
		createPanel.add(new JLabel("/"));
		createPanel.add(revText);
		createPanel.add(new JLabel("."));
		createPanel.add(nameText);
		createPanel.add(assignButton);
		propertyPanel.add("1.2.center.center", createPanel);
		
		propertyPanel.add("2.1.center.center", new JLabel("关联加工工艺ID/版本.名称："));
		JPanel relationPanel = new JPanel();
		JTextField idText = new JTextField(10);
		idText.setEditable(false);
		idText.setText(processRev.getProperty("item_id"));
		JTextField revText = new JTextField(2);
		revText.setEditable(false);
		revText.setText(processRev.getProperty("item_revision_id"));
		JTextField nameText = new JTextField(12);
		nameText.setEditable(false);
		nameText.setText(name);
		relationPanel.add(idText);
		relationPanel.add(new JLabel("/"));
		relationPanel.add(revText);
		relationPanel.add(new JLabel("."));
		relationPanel.add(nameText);
		propertyPanel.add("2.2.center.center", relationPanel);
		
		JPanel buttonPanel = new JPanel();
		okButton = new JButton("确认");
		closeButton = new JButton("关闭");
		okButton.addActionListener(this);
		closeButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(closeButton);
		setLayout(new BorderLayout());
		add(propertyPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		centerToScreen();
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object obj = event.getSource();
		try {
			if (obj.equals(okButton)) {
				String id = idText.getText();
				String revID = revText.getText();
				if (id.isEmpty() || revID.isEmpty()) {
					MessageBox.post(this, "请指派ID", "提示", MessageBox.INFORMATION);
					return;
				}
				TCComponentItem process = itemType.create(id, revID, type, nameText.getText(), "", null);
				TCComponentItemRevision rev = process.getLatestItemRevision();
				session.getUser().getHomeFolder().add("contents", rev);
				setSummaryProcess(rev);
				processRev.add("AE8_ASSONCMEP", rev);
				
			} else if (obj.equals(assignButton)) {
				assign();
			} else if (obj.equals(closeButton)) {
				dispose();
			}
		} catch (Exception e) {
			MessageBox.post(this, e);
			e.printStackTrace();
		}
		
	}
	
	public void setSummaryProcess(TCComponentItemRevision rev) throws TCException {
		TCComponentItemRevision topRev = getTopREvision();
		if (topRev != null) {
			rev.setRelated("IMAN_METarget", topRev.getRelatedComponents("IMAN_METarget"));
			TCComponentRevisionRule rule = new TCComponentRevisionRule();//创建规则
			TCComponentBOMWindowType type = (TCComponentBOMWindowType) session.getTypeComponent("BOPWindow");//获取BOM窗口类型
			TCComponentBOMWindow window = type.create(rule);//创建一个BOM窗口
			TCComponentBOMLine topLine = window.setWindowTopLine(topRev.getItem(),topRev,null,null);//设置顶层BOMLine
			topLine.addBOMLine(topLine, rev, null);
		}
		
	}
	
	public TCComponentItemRevision getTopREvision() throws TCException {
		TCComponent[] topComs = processRev.whereUsed((short) 0);
		TCComponentItemRevision rev = null;
		if (topComs != null && topComs.length > 0) {
			TCComponent topCom = topComs[0];
			if (topCom instanceof TCComponentItem) {
				rev = ((TCComponentItem) topCom).getLatestItemRevision();
			} else if (topCom instanceof TCComponentItemRevision) {
				rev = (TCComponentItemRevision) topCom;
			}
		}
		return rev;
	}

	public void assign() throws TCException {
		String id = idText.getText();
		String revID = revText.getText();
		if (id.isEmpty() && revID.isEmpty()) {
			id = itemType.getNewID();
			revID = itemType.getNewRev(null);
			idText.setText(id);
			revText.setText(revID);
			idText.setEditable(false);
			revText.setEditable(false);
		}
	}
}
