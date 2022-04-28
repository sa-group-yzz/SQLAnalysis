package top.viewv.analysis;

import edu.usc.sql.graphs.Node;
import edu.usc.sql.graphs.NodeInterface;
import edu.usc.sql.graphs.cfg.CFGInterface;
import soot.Unit;
import top.viewv.abstraction.Codepoint;
import top.viewv.analysis.helper.Reachability;
import top.viewv.function.Analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReachabilityAnalysis {
	private Codepoint codepoint;
	private Map<NodeInterface, Codepoint> nodeToTargetCodepoints = new HashMap<>();
	private Set<Codepoint> reachableCodepoints = new HashSet<>();
	private Map<NodeInterface, Set<Codepoint>> methodToContainingCodepoints = new HashMap<>();
	
	public ReachabilityAnalysis(Codepoint codepoint, Set<Codepoint> targetCodepoints, Map<CFGInterface, Reachability> cachedReachability)
	{
		this.codepoint = codepoint;
		for(Codepoint target : targetCodepoints)
		{
			nodeToTargetCodepoints.put(target.getNode(), target);
		}
		

		for(Codepoint targetCodepoint : targetCodepoints)
		{
			for(NodeInterface callSiteNode : targetCodepoint.getCallChain().keySet())
			{
				//exclude the dummy entry node
				if(((Node<Unit>) callSiteNode).getActualNode() != null)
				{
					if(methodToContainingCodepoints.containsKey(callSiteNode))
					{
						methodToContainingCodepoints.get(callSiteNode).add(targetCodepoint);
					}
					else
					{
						Set<Codepoint> reachableCodepoint = new HashSet<>();
						reachableCodepoint.add(targetCodepoint);
						methodToContainingCodepoints.put(callSiteNode, reachableCodepoint);
					}
				}
			}
		}
		//CFGInterface entry = silica.getCallChain().entrySet().iterator().next().getValue();
		
		//computeReachability(entry);
		
		/*
		Entry<NodeInterface, CFGInterface> last = null;
		for(Entry<NodeInterface, CFGInterface> en : silica.getCallChain().entrySet())
		{26
			last = en;
		}
		CFGInterface parentCFG = last.getValue();
		computeReachability(parentCFG);
		*/
		computeInterReachability(cachedReachability);
	}
	

	/**
	 * Return a set of silicas that the input silica can reach.
	 * Note: although the input silica is reachable to itself in theory, 
	 * we do not add it to the reachable set
	 */
	public Set<Codepoint> getReachableCodepoints()
	{
		return reachableCodepoints;
	}

	
	private void computeReachability(CFGInterface cfg) {
		
		Reachability reach = new Reachability(cfg.getAllNodes(), cfg.getAllEdges(), cfg.getEntryNode());
		
		for(NodeInterface n : cfg.getAllNodes())
		{
			if(n.equals(codepoint.getNode()))
			{
				for(NodeInterface reachableNode : reach.getReachableTable().get(n))
				{
					if(nodeToTargetCodepoints.containsKey(reachableNode) && !reachableNode.equals(codepoint.getNode()))
					{
						reachableCodepoints.add(nodeToTargetCodepoints.get(reachableNode));
					}
				}
			}	
		}
	}
	
	
	private void computeInterReachability(Map<CFGInterface, Reachability> cachedReachability)
	{
		for(CFGInterface cfg : codepoint.getCallChain().values())
		{
			Reachability reach;
			if(cachedReachability.containsKey(cfg))
			{
				reach = cachedReachability.get(cfg);
			}
			else
			{
				reach = new Reachability(cfg.getAllNodes(), cfg.getAllEdges(), cfg.getEntryNode());
				if(Analyzer.isCached)
					cachedReachability.put(cfg, reach);
			}
			
			for(NodeInterface n : cfg.getAllNodes())
			{
				//get the node that the silica node can reach or its call site nodes can reach	
				if(n.equals(codepoint.getNode()) || codepoint.getCallChain().keySet().contains(n))
				{
					for(NodeInterface reachableNode : reach.getReachableTable().get(n))
					{
						//if can reach a target silica
						if(nodeToTargetCodepoints.containsKey(reachableNode) && !reachableNode.equals(codepoint.getNode()))
						{
							reachableCodepoints.add(nodeToTargetCodepoints.get(reachableNode));
						}
						//if can reach a call site of a target silica
						else if(methodToContainingCodepoints.containsKey(reachableNode) && !codepoint.getCallChain().containsKey(reachableNode))
						{
							reachableCodepoints.addAll(methodToContainingCodepoints.get(reachableNode));
						}
					}
				}
					
			}
		}
	}
}
