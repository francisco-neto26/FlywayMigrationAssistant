package com.supergestao.FlywayMigrationAssistant.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class DiretorioTreeNode extends DefaultMutableTreeNode {
    private boolean carregado = false;
    private final File arquivo;

    public DiretorioTreeNode(File arquivo) {
        super(arquivo.getName());
        this.arquivo = arquivo;
        if (arquivo.isDirectory()) {
            File[] subpastas = arquivo.listFiles(File::isDirectory);
            if (subpastas != null && subpastas.length > 0) {
                add(new DefaultMutableTreeNode("Carregando..."));
            }
        }
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
    private boolean temSubdiretorios(File diretorio) {
        File[] subpastas = diretorio.listFiles(File::isDirectory);
        return subpastas != null && subpastas.length > 0;
    }
    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }
}
