package top.viewv.analysis;

import edu.usc.sql.graphs.EdgeInterface;
import edu.usc.sql.graphs.NodeInterface;
import edu.usc.sql.graphs.cfg.CFGInterface;
import top.viewv.abstraction.Codepoint;
import top.viewv.analysis.helper.ControlDependenceGraph;
import top.viewv.analysis.helper.DuplicateCallChainBeforeNode;
import top.viewv.analysis.helper.Pair;
import top.viewv.function.Analyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlDependentAnalysis {
	private Codepoint codepoint;
	private Set<Pair<Codepoint,Integer>> controlDependentSet = new HashSet<>();
	
	public ControlDependentAnalysis(Codepoint codepoint, Map<CFGInterface, ControlDependenceGraph> cachedControlDependenceGraph)
	{
		this.codepoint = codepoint;
		for(CFGInterface cfg : codepoint.getCallChain().values())
		{
			ControlDependenceGraph cdg;
			if(cachedControlDependenceGraph.containsKey(cfg))
				cdg = cachedControlDependenceGraph.get(cfg);
			else
			{
				cdg = new ControlDependenceGraph(cfg);
				if(Analyzer.isCached)
					cachedControlDependenceGraph.put(cfg, cdg);
			}

			//removeExceptionBlock(cfg);
			for(NodeInterface n : cfg.getAllNodes())
			{
				if(codepoint.getCallChain().containsKey(n))
				{
					if(cdg.getControlDependents(n) != null)
					{
						for(EdgeInterface controlDependentEdge : cdg.getControlDependents(n))
						{
							Codepoint controlDependentCodepoint = new Codepoint(controlDependentEdge.getSource(),
									DuplicateCallChainBeforeNode.duplicate(codepoint.getCallChain(), n));
							controlDependentSet.add(new Pair<Codepoint,Integer>(controlDependentCodepoint, controlDependentEdge.hashCode()));
						}
					}
				}
				else if(n.equals(codepoint.getNode()))
				{
					if(cdg.getControlDependents(n) != null)
					{
						for(EdgeInterface controlDependentEdge : cdg.getControlDependents(n))
						{
							Codepoint controlDependentCodepoint = new Codepoint(controlDependentEdge.getSource(), codepoint.getCallChain());
							controlDependentSet.add(new Pair<Codepoint,Integer>(controlDependentCodepoint, controlDependentEdge.hashCode()));
						}
					}
				}
			}

		}
	}
	
	public Set<Pair<Codepoint,Integer>> getControlDependentSet()
	{
		return controlDependentSet;
	}

}
