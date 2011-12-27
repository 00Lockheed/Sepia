package edu.cwru.SimpleRTS.agent;

import java.util.Map;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.resource.ResourceType;

/**
 * This is a wrapper class for calling agent implemented in Matlab.
 * @author Feng
 *
 */
public class MatlabAgent extends Agent {

	private static final long serialVersionUID = -1819028685122969080L;
	
	MatlabProxy m_proxy;
	
	public MatlabAgent(int playernum) {
		super(playernum);
		initMatlabCon();
		try {
			m_proxy.feval("agent_init", playernum);
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
	}
	
	protected void initMatlabCon() {
		//Set options for matlab proxy factory
        MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).build();

        //Create a proxy, which we will use to control MATLAB
        MatlabProxyFactory proxyFactory = new MatlabProxyFactory(options);
        try {
            m_proxy = proxyFactory.getProxy();

        } catch (MatlabConnectionException e) {
            System.err.println("Create Matlab Proxy failed!");
            e.printStackTrace();
        }
	}

	@Override
	public Builder<Integer, Action> initialStep(StateView newstate) {
		ImmutableMap.Builder<Integer,Action> builder = new ImmutableMap.Builder<Integer,Action>();
		try {
			Object[] objects = m_proxy.returningFeval("agent_initialStep", 1, newstate);
			
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
		return builder;
	}

	@Override
	public Builder<Integer, Action> middleStep(StateView newstate) {
		ImmutableMap.Builder<Integer,Action> builder = new ImmutableMap.Builder<Integer,Action>();
		try {
			Object[] objects = m_proxy.returningFeval("agent_middleStep", 1, newstate);
			Map<Double, Action> actionMap = (Map<Double, Action>)objects[0];
			for(double key : actionMap.keySet()) {
				builder.put((int)key, actionMap.get(key));
			}
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
		return builder;
	}

	@Override
	public void terminalStep(StateView newstate) {
		try {
			m_proxy.feval("agent_terminalStep", newstate);
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
	}

}