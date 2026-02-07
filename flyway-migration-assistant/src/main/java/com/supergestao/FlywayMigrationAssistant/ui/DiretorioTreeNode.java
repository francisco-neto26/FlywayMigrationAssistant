package com.supergestao.FlywayMigrationAssistant.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class DiretorioTreeNode extends DefaultMutableTreeNode {
    private final File arquivo;
    private boolean carregado = false;

    public DiretorioTreeNode(File arquivo) {
        super(arquivo, true);
        this.arquivo = arquivo;
    }

    public File getFile() {
        return arquivo;
    }

    public boolean isCarregado() {
        return carregado;
    }

    public void setCarregado(boolean carregado) {
        this.carregado = carregado;
    }

    @Override
    public boolean isLeaf() {
        return !arquivo.isDirectory();
    }

    @Override
    public String toString() {
        return arquivo.getName();
    }
}