/*
 * [The "BSD license"]
 * Copyright (c) 2011, abego Software GmbH, Germany (http://www.abego.org)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the abego Software GmbH nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;

/**
 * A JComponent displaying a tree of TextInBoxes, given by a {@link TreeLayout}.
 *
 * @author Udo Borkowski (ub@abego.org)
 */
@SuppressWarnings("serial")
public class TextInBoxTreePane extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {
		private double zoom = 0.5;
		//private JScrollPane scrollPane;
		private Stack<TextInBox> drawRoot;
		private TextInBox selected;
		
		private Point viewport;
		
		public void init(JScrollPane pane){
			//this.scrollPane = pane;
		}
		
		@Override
		public Dimension getPreferredSize(){
			double width = 0;
			for(int i = 0; i < treeLayout.getLevelCount(); i++){
				width += treeLayout.getSizeOfLevel(i) + treeLayout.getConfiguration().getGapBetweenLevels(i + 1);
			}
			width *= zoom;
			
			TextInBox bottomMost = drawRoot.peek();
			for(TextInBox t : getChildren(drawRoot.peek())){
				bottomMost = t;
			}
			Rectangle2D bounds = getBoundsOfNode(bottomMost);
			double height = bounds.getY() + bounds.getHeight();
			height *= zoom;
			
			return new Dimension((int)width, (int)height);
		}
		
		public void setTree(TreeLayout<TextInBox> newTree){
            this.treeLayout = newTree;
            this.selected = null;
             
            drawRoot = new Stack<TextInBox>();
            drawRoot.push(getTree().getRoot());
            repaint();
		}
		
        private TreeLayout<TextInBox> treeLayout;

        private TreeForTreeLayout<TextInBox> getTree() {
                return treeLayout.getTree();
        }

        private Iterable<TextInBox> getChildren(TextInBox parent) {
                return getTree().getChildren(parent);
        }

        private Rectangle2D.Double getBoundsOfNode(TextInBox node) {
                return treeLayout.getNodeBounds().get(node);
        }

        /**
         * Specifies the tree to be displayed by passing in a {@link TreeLayout} for
         * that tree.
         *
         * @param treeLayout
         */
        public TextInBoxTreePane(TreeLayout<TextInBox> treeLayout) {
                setTree(treeLayout);
                this.viewport = new Point(0, 0);
                this.addMouseListener(this);
                this.addMouseMotionListener(this);
                this.addMouseWheelListener(this);

                Dimension size = treeLayout.getBounds().getBounds().getSize();
                setPreferredSize(size);
        }

        // -------------------------------------------------------------------
        // painting

        private final static int ARC_SIZE = 10;
        private final static Color BOX_COLOR = Color.orange;
        private final static Color BOX_BUILD_COLOR = Color.blue;
        private final static Color BORDER_COLOR = Color.darkGray;
        private final static Color FINAL_BORDER_COLOR = Color.red;
        private final static Color TEXT_COLOR = Color.black;

        private void paintEdges(Graphics g, TextInBox parent) {
                if (!getTree().isLeaf(parent)) {
                        Rectangle2D.Double b1 = getBoundsOfNode(parent);
                        double x1 = b1.getCenterX();
                        double y1 = b1.getCenterY();
                        for (TextInBox child : getChildren(parent)) {
                                Rectangle2D.Double b2 = getBoundsOfNode(child);
                                g.drawLine((int) x1, (int) y1, (int) b2.getCenterX(),
                                                (int) b2.getCenterY());

                                paintEdges(g, child);
                        }
                }
        }

        private void paintBox(Graphics g, TextInBox textInBox) {
                // draw the box in the background
        		Graphics2D graph = (Graphics2D) g;
        		
        		if(textInBox.equals(selected)){
        			graph.setColor(Color.red);
        		}
        		else if(textInBox.source.getPrice_create() > 0){
        			graph.setColor(BOX_BUILD_COLOR);
        		}
        		else{
        			graph.setColor(BOX_COLOR);
        		}
        		
        		Rectangle2D.Double box = getBoundsOfNode(textInBox);
                graph.fillRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
                                (int) box.height - 1, ARC_SIZE, ARC_SIZE);
                Stroke o = graph.getStroke();
                if(textInBox.source.isWeapon_final()){
                	graph.setStroke(new BasicStroke(2));
                	graph.setColor(FINAL_BORDER_COLOR);
                }
                else{
                	graph.setColor(BORDER_COLOR);
                }
                graph.drawRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
                                (int) box.height - 1, ARC_SIZE, ARC_SIZE);
                
                graph.setStroke(o);
                // draw the text on top of the box (possibly multiple lines)
                g.setColor(TEXT_COLOR);
                String[] lines = textInBox.text.split("\n");
                FontMetrics m = getFontMetrics(getFont());
                
                double center = box.x + box.width / 2;// - ARC_SIZE / 2;
                center = center - m.stringWidth(textInBox.text) / 2;
                //center = center - m.getMaxAdvance() / 2;
                
                
                int x = (int) center;
                int y = (int) box.y + m.getAscent() + m.getLeading() + 1;
                for (int i = 0; i < lines.length; i++) {
                        g.drawString(lines[i], x, y);
                        y += m.getHeight();
                }
        }

        @Override
        public void paint(Graphics g) {
        		Graphics2D graph = (Graphics2D) g;
        		
        		graph.translate(viewport.getX(), viewport.getY());
                graph.scale(zoom, zoom);
                
        		super.paint(g);
                
                paintEdges(g, drawRoot.peek());//getTree().getRoot());

                List<TextInBox> nodes = new LinkedList<TextInBox>();
                
                nodes.add(drawRoot.peek());
                while(!nodes.isEmpty()){
                	TextInBox current = nodes.get(0);
                	nodes.remove(0);
                	for(TextInBox c : getChildren(current)) nodes.add(c);
                	paintBox(g, current);
                }
                
                //helper to find the mouse position
                g.fillRect((int)(click_x - 5), (int)(click_y - 5), 10, 10);
        }
        
        //-----------------------mouse scrolling

        //private double mouse_x_start;
        //private double mouse_y_start;
        
        private double mouse_x_diff;
        private double mouse_y_diff;
        
        private double click_x;
        private double click_y;
        
		@Override
		public void mouseClicked(MouseEvent e) {
			click_x = e.getX();
			click_y = e.getY();
			
			TextInBox sel = getAt(click_x, click_y);
			if(selected != null && selected.equals(sel)){
				drawRoot.push(sel);
			}
			else{
				selected = sel;
			}
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouse_x_diff = e.getX() - viewport.getX();
			mouse_y_diff = e.getY() - viewport.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			viewport = new Point((int)(e.getX() - mouse_x_diff), (int)(e.getY() - mouse_y_diff));
			repaint();
			/*
			
			Point cPos = viewport;
			double xDiff = e.getX() - mouse_x_start;
			double yDiff = e.getY() - mouse_y_start;
			
			double xPos = cPos.getX() + xDiff * zoom;
			double yPos = cPos.getY() + yDiff * zoom;
			viewport = new Point((int)(xPos), (int)(yPos));
			repaint();

			mouse_x_start = e.getX();
			mouse_y_start = e.getY();*/
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			//determine current position in the world
			double pos_x = e.getX();
			double pos_y = e.getY();
			
			Point pPos = viewport;
			
			double xdiff = pos_x - pPos.getX();
			double ydiff = pos_y - pPos.getY();
			xdiff /= zoom;
			ydiff /= zoom;
			
			zoom = Math.min(2, Math.max(0.1, zoom - 0.1 * e.getWheelRotation()));
			
			xdiff *= zoom;
			ydiff *= zoom;
			
			xdiff = pos_x - xdiff;
			ydiff = pos_y - ydiff;
			Point nextViewPort = new Point((int) xdiff, (int) ydiff);
			
			viewport = nextViewPort;
			
			this.repaint();
		}
		
		//-----------------------------more functionality
		private TextInBox getAt(double x, double y){
			for(TextInBox t : treeLayout.getNodeBounds().keySet()){
				Rectangle2D bounds = getBoundsOfNode(t);
				
				double bx = bounds.getX() * zoom;
				double by = bounds.getY() * zoom;
				double bx2 = (bounds.getX() + bounds.getWidth()) * zoom;
				double by2 = (bounds.getY() + bounds.getHeight()) * zoom;
				
				
				if(x >= bx && x <= bx2 && y >= by && y <= by2){
					return t;
				}
			}
			return null;
		}
		
		//external methods
		
		public void back(){
			if(!drawRoot.peek().equals(getTree().getRoot())){
				drawRoot.pop();
				repaint();
			}
		}
}
