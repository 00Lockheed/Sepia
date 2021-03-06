% This function will be called at the end of each episode.
% Nothing is returned here.
function [] = agent_terminalStep( state )

import edu.cwru.sepia.action.*;
import edu.cwru.sepia.environment.*;
import edu.cwru.sepia.model.*;
import edu.cwru.sepia.model.resource.*;
import edu.cwru.sepia.model.unit.*;

currentGold = state.getResourceAmount(0, ResourceType.GOLD).intValue();
currentWood = state.getResourceAmount(0, ResourceType.WOOD).intValue();
fprintf('\nCurrent Gold: %d\n', currentGold);
fprintf('Current Wood: %d\n', currentWood);
fprintf('Congratulations! You finish the task');

end

