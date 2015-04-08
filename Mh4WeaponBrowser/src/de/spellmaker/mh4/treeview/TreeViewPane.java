package de.spellmaker.mh4.treeview;

import java.awt.BasicStroke;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import javax.swing.JComponent;

import org.abego.treelayout.TreeLayout;


@SuppressWarnings("serial")
public class TreeViewPane<N> extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener{
	private TreeLayout<N> tree;
	private double zoom;
	//top leftmost point
	private Point viewport;
	//for translating
	private double mouse_x_diff;
	private double mouse_y_diff;
	
	private NodeDesigner<N> nodeDesigner;
	
	private N selected;
	
	private List<ElementSelectedListener<N>> listeners;
	
	private Point clickToWorld(double x, double y){
    	double nx = (x - viewport.getX()) / zoom;
    	double ny = (y - viewport.getY()) / zoom;
    	
    	return new Point((int)(nx), (int)(ny));
	}
	
	private N nodeAt(double x, double y){
		for(N t : tree.getNodeBounds().keySet()){
			Rectangle2D bounds = tree.getNodeBounds().get(t);
			
			double bx = bounds.getX();
			double by = bounds.getY();
			double bx2 = (bounds.getX() + bounds.getWidth());
			double by2 = (bounds.getY() + bounds.getHeight());
			
			if(x >= bx && x <= bx2 && y >= by && y <= by2){
				return t;
			}
		}
		return null;
	}
	
	public TreeViewPane(){
		zoom = 0.5;
		viewport = new Point(0, 0);
		tree = null;
		
		listeners = new ArrayList<ElementSelectedListener<N>>();
		nodeDesigner = new DefaultNodeDesigner<N>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	public void addElementSelectedListener(ElementSelectedListener<N> l){
		this.listeners.add(l);
	}
	
	public void setTree(TreeLayout<N> tree){
		this.tree = tree;
		this.selected = null;
		this.viewport = new Point(0, 0);
		ElementSelectedEvent<N> event = new ElementSelectedEvent<N>(MouseEvent.BUTTON1, null);
		for(ElementSelectedListener<N> l : listeners) l.elementSelected(event);
		repaint();
	}
	
	public void setNodeDesigner(NodeDesigner<N> designer){
		this.nodeDesigner = designer;
	}

	public void scrollTo(N searchText){
		for(Entry<N, Rectangle2D.Double> entry : tree.getNodeBounds().entrySet()){
			if(entry.getKey().toString().contains(searchText.toString())){
				double x = entry.getValue().getX() * zoom - getWidth() / 2;
				double y = entry.getValue().getY() * zoom - getHeight() / 2;
				
				viewport = new Point((int) -x, (int) -y);
				selected = entry.getKey();
				repaint();
				break;
			}
		}
	}
	
	@Override
	public void paint(Graphics g){
		Graphics2D graph = (Graphics2D) g;
		
		graph.translate(viewport.getX(), viewport.getY());
		graph.scale(zoom, zoom);
		
		super.paint(graph);
		
		paintEdges(graph, tree.getTree().getRoot());
		
		Queue<N> nodes = new LinkedList<N>();
		nodes.add(tree.getTree().getRoot());
		while(!nodes.isEmpty()){
			N c = nodes.poll();
			paintBox(graph, c);
			for(N n : tree.getTree().getChildren(c)){
				nodes.add(n);
			}
		}
	}
	
	private void paintEdges(Graphics2D g, N parent){
		if(!tree.getTree().isLeaf(parent)){
			Rectangle2D.Double b1 = tree.getNodeBounds().get(parent);
	        double x1 = b1.getCenterX();
	        double y1 = b1.getCenterY();
	        for (N child : tree.getTree().getChildren(parent)) {
	        	//TODO: insert node designer
	        	Rectangle2D.Double b2 = tree.getNodeBounds().get(child);
	            g.drawLine((int) x1, (int) y1, (int) b2.getCenterX(), (int) b2.getCenterY());
	            paintEdges(g, child);
	        }
		}
	}
	
	private void paintBox(Graphics2D g, N n){
		NodeDesign design = nodeDesigner.getNodeDesign(n, selected != null && selected.equals(n));
		if(design.DISPLAY){
			g.setColor(design.BOX_COLOR);
			
			Rectangle2D.Double box = tree.getNodeBounds().get(n);
	        g.fillRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
	                        (int) box.height - 1, design.ARC_SIZE, design.ARC_SIZE);
	        Stroke o = g.getStroke();
	        g.setStroke(new BasicStroke(design.LINE_WIDTH));
	        g.setColor(design.LINE_COLOR);
	        
	        g.drawRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
	                        (int) box.height - 1, design.ARC_SIZE, design.ARC_SIZE);
	        
	        g.setStroke(o);
	        // draw the text on top of the box (possibly multiple lines)
	        g.setColor(design.TEXT_COLOR);
	        String nodeText = n.toString();
	        String[] lines = nodeText.split("\n");
	        FontMetrics m = getFontMetrics(getFont());
	        
	        double center = box.x + box.width / 2;
	        center = center - m.stringWidth(nodeText) / 2;
	   
	        int x = (int) center;
	        int y = (int) box.y + m.getAscent() + m.getLeading() + 1;
	        for (int i = 0; i < lines.length; i++) {
	                g.drawString(lines[i], x, y);
	                y += m.getHeight();
	        }
		}
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

	@Override
	public void mouseDragged(MouseEvent e) {
		viewport = new Point((int)(e.getX() - mouse_x_diff), (int)(e.getY() - mouse_y_diff));
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point world = clickToWorld(e.getX(), e.getY());
		double click_x = world.getX();
		double click_y = world.getY();
		
		N sel = nodeAt(click_x, click_y);
		ElementSelectedEvent<N> event = new ElementSelectedEvent<N>(e.getButton(), sel);
		if(selected != null && selected.equals(sel)){
			for(ElementSelectedListener<N> l : listeners) l.elementDoubleClicked(event);
		}
		else{
			for(ElementSelectedListener<N> l : listeners) l.elementSelected(event);
		}
		selected = sel;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse_x_diff = e.getX() - viewport.getX();
		mouse_y_diff = e.getY() - viewport.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
