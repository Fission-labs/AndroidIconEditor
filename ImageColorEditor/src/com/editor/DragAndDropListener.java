package com.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragAndDropListener implements DropTargetListener {

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        System.out.println("dragEnter");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub
        System.out.println("dragExit");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        System.out.println("dragOver");
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        // TODO Auto-generated method stub
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        Transferable t = dtde.getTransferable();
        try {
            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            if (files.size() == 1) {
                MainUi.getInstance().setImageIcon(files.get(0));
            }
        } catch (UnsupportedFlavorException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("drop");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        System.out.println("dropActionChanged");
    }

}
