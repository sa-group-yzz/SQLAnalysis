package top.viewv.analysis;

import edu.usc.sql.graphs.EdgeInterface;
import edu.usc.sql.graphs.Node;
import edu.usc.sql.graphs.NodeInterface;
import edu.usc.sql.graphs.cfg.CFGInterface;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import top.viewv.abstraction.Codepoint;
import top.viewv.analysis.helper.DuplicateCallChainBeforeNode;
import top.viewv.analysis.helper.LayerRegion;
import top.viewv.analysis.helper.RegionNode;
import top.viewv.function.Analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoopAnalysis {

	private Codepoint codepoint;
	private Set<Codepoint> loopSet = new HashSet<>();
	
	public LoopAnalysis(Codepoint codepoint, Map<CFGInterface, LayerRegion> cachedRegionTree)
	{
		this.codepoint = codepoint;
		for(CFGInterface cfg : codepoint.getCallChain().values())
		{
			Map<RegionNode, Boolean> markedforRTree = new HashMap<>();
			Map<EdgeInterface,RegionNode> backedges = new HashMap<>();
			
			LayerRegion lr;
			if(cachedRegionTree.containsKey(cfg))
				lr = cachedRegionTree.get(cfg);
			else
			{
				lr = new LayerRegion(cfg);
				if(Analyzer.isCached)
					cachedRegionTree.put(cfg, lr);
			}
			RegionNode root = lr.getRoot();

			//removeExceptionBlock(cfg);
			
			for (RegionNode rn : lr.getAllRegionNode()) {		
				markedforRTree.put(rn, false);
				backedges.put(rn.getBackEdge(),rn);
			}
			
			dfsRegionTree(root, cfg.getSignature(), markedforRTree);
		}
	}
	
	public Set<Codepoint> getLoopSet()
	{
		return loopSet;
	}
	
	private void dfsRegionTree(RegionNode root,
			String currentMethodSig, Map<RegionNode, Boolean> markedforRTree)
	{
		markedforRTree.put(root, true);
		for (RegionNode child : root.getChildren()) {
			if (!markedforRTree.get(child))
			{				
				dfsRegionTree(child, currentMethodSig, markedforRTree);
			}					
	
		}	
		addToLoopSet(root, currentMethodSig);
	}
	

	private void addToLoopSet(RegionNode rn, String currentMethodSig)
	{
		if(rn.getBackEdge() != null)
		{
			for(NodeInterface n : rn.getNodeList())
			{
				Unit actualNode = (Unit) ((Node)n).getActualNode();
				if(actualNode!=null)
				{
					Codepoint loopHeaderCodepoint;
					if(n.equals(codepoint.getNode()))
					{
						loopHeaderCodepoint = new Codepoint(getHeader(rn, currentMethodSig), codepoint.getCallChain());
						loopSet.add(loopHeaderCodepoint);
					}
					else if(codepoint.getCallChain().containsKey(n))
					{
						loopHeaderCodepoint = new Codepoint(getHeader(rn, currentMethodSig),
								DuplicateCallChainBeforeNode.duplicate(codepoint.getCallChain(), n));
						loopSet.add(loopHeaderCodepoint);
					}
				}
			}
		}
	}

	//a dangerous move to handle do-while loop
	private NodeInterface getHeader(RegionNode regionNode, String s)
	{
		EdgeInterface backEdge = regionNode.getBackEdge();
		Stmt source = (Stmt) ((Node)backEdge.getSource()).getActualNode();
		Stmt destination = (Stmt) ((Node)backEdge.getDestination()).getActualNode();

		if(destination instanceof IfStmt)
			return backEdge.getDestination();
		else
		{
			if(source instanceof IfStmt)
				return backEdge.getSource();
			else
			{
				System.err.println("Both source and destination of the loop backedge are not an If statement");
				System.err.println(s);
				System.err.println("Source:" + source);
				System.err.println("Des:" + destination);
				return backEdge.getDestination();
			}
		}
	}
}

