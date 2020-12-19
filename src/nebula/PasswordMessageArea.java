package nebula;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class PasswordMessageArea extends MessageArea {

	public PasswordMessageArea(Dialog parent) {
		super(parent);
	}
	
	@Override
	protected void createTextBox() {
		final Text textbox = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		textbox.setText(textBoxValue);
		textbox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		textbox.setLayoutData(gd);
		textbox.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				textBoxValue = textbox.getText();
			}
		});

		textbox.addListener(SWT.KeyUp, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					PasswordMessageArea.this.parent.shell.dispose();
					PasswordMessageArea.this.parent.getFooterArea().selectedButtonIndex = 0;
				}
			}
		});

		textbox.getShell().addListener(SWT.Activate, new Listener() {

			@Override
			public void handleEvent(final Event arg0) {
				textbox.forceFocus();
				textbox.setSelection(textbox.getText().length());
				textbox.getShell().removeListener(SWT.Activate, this);
			}
		});

	}

}
