package org.docs.rule;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class SingleRuleDoc {
    private static final String RULE = "fakePlayerMaxCraftCount";

    // 将一条规则的信息写入剪贴板
    public static void main(String[] args) throws IOException, NoSuchFieldException, ClassNotFoundException, UnsupportedFlavorException {
        RuleDocument ruleDocument = new RuleDocument();
        RuleInformation info = ruleDocument.readClass(RULE);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String substring = info.toString().substring(4);
        System.out.println(substring);
        Transferable contents = clipboard.getContents(null);
        // 避免重复写入
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            if (contents.getTransferData(DataFlavor.stringFlavor).equals(substring)) {
                return;
            }
        }
        // 写入系统剪贴板
        clipboard.setContents(new StringSelection(substring), null);
    }
}
