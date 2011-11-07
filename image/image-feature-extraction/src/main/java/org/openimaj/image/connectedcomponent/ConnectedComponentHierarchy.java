package org.openimaj.image.connectedcomponent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.pixel.ConnectedComponent.ConnectMode;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.util.tree.TreeNode;
import org.openimaj.util.tree.TreeNodeImpl;


class ChildWeightedTreeNode<T> extends TreeNodeImpl<T> implements Comparable<ChildWeightedTreeNode<T>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5430956987739017813L;
	List<ChildWeightedTreeNode<T>> parents = new ArrayList<ChildWeightedTreeNode<T>>();

	@Override
	public int compareTo(ChildWeightedTreeNode<T> that) {
		return this.children.size() > that.children.size() ? -1 : 1;
	}
	
	@Override
	public String toString(){
		return String.format("Got %d children", this.children.size());
	}

	public void addParent(ChildWeightedTreeNode<T> outerNode) {
		this.parents.add(outerNode);
	}
}

public class ConnectedComponentHierarchy {
	private static final float MINIMUM_HEIGHT = 5;
	private static final float MINIMUM_WIDTH = 5;
	private int MINIMUM_AREA = 5;
	private ConnectedComponentLabeler labler;
	
	public ConnectedComponentHierarchy(ConnectedComponentLabeler labeler, int threshold){
		this.labler = labeler;
		this.MINIMUM_AREA = threshold;
	}
	
	public TreeNode<ConnectedComponent> hierarchy(FImage markerImage){
		long start = System.currentTimeMillis();
		markerImage.threshold(0.5f);
		List<ConnectedComponent> components = labler.findComponents(markerImage);
		List<ConnectedComponent> invComponents = labler.findComponents(markerImage.inverse());
		components.addAll(invComponents);
//		List<Pair<ConnectedComponent>> containsList = new ArrayList<Pair<ConnectedComponent>>();
		List<ChildWeightedTreeNode<ConnectedComponent>> nodeList = new ArrayList<ChildWeightedTreeNode<ConnectedComponent>>();
		for (int i = 0; i < components.size(); i++) {
			ConnectedComponent ith = components.get(i);
			Rectangle ithPoly = ith.calculateRegularBoundingBox();
			if(ithPoly.calculateArea() < MINIMUM_AREA || ithPoly.x < MINIMUM_WIDTH || ithPoly.y < MINIMUM_HEIGHT) {
				components.remove(i);
				i--;
				continue;
			}
			ChildWeightedTreeNode<ConnectedComponent> node = new ChildWeightedTreeNode<ConnectedComponent>();
			node.value = ith;
			nodeList.add(node);
		}
		
//		MBFImage forDisplay = new MBFImage(markerImage.clone(),markerImage.clone(),markerImage.clone());
//		SegmentationUtilities.renderSegments(forDisplay, components);
//		DisplayUtilities.display(forDisplay);
		
		int i = 0;
		for(ConnectedComponent outer: components){
			Rectangle outerBox = outer.calculateRegularBoundingBox();
			ChildWeightedTreeNode<ConnectedComponent> outerNode = nodeList.get(i);
			int j = 0;
			for(ConnectedComponent inner : components){
				if(inner != outer){
					Rectangle innerBox = inner.calculateRegularBoundingBox();
					if(outerBox.isInside(innerBox)){
						ChildWeightedTreeNode<ConnectedComponent> innerNode = nodeList.get(j);
						outerNode.addChild(innerNode);
						innerNode.addParent(outerNode);	
					}
				}
				j++;
			}
			i++;
		}
		
		Collections.sort(nodeList);
		TreeNode<ConnectedComponent> root = new ChildWeightedTreeNode<ConnectedComponent>();
		for (ChildWeightedTreeNode<ConnectedComponent> node : nodeList) {
			// part of the root if it has no parents
			if(node.parents.size() == 0){
				root.addChild(node);
			}
			for(i = 0; i < node.children.size(); i++){
				ChildWeightedTreeNode<ConnectedComponent> child = (ChildWeightedTreeNode<ConnectedComponent>) node.children.get(i); 
				if(child.parents.size() == 1){
					// This child is a direct child, do nothing
				}
				else{
					child.parents.remove(node); // EXPENSIVE (FIXME)
					node.children.remove(i--); // cheap
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Total time: " + (end - start));
		return root;
	}
	
	public static void main(String[] args) throws IOException {
		String markerFile = "/Users/ss/Desktop/marker-profile.jpeg";
		FImage markerImage = ImageUtilities.readF(new File(markerFile));
		
//		ConnectedComponentLabeler.DEFAULT_ALGORITHM = ConnectedComponentLabeler.Algorithm.FLOOD_FILL;
		ConnectedComponentLabeler labler = new ConnectedComponentLabeler(ConnectMode.CONNECT_4);
		ConnectedComponentHierarchy hier = new ConnectedComponentHierarchy(labler,20);
		hier.hierarchy(markerImage);
	}
}
