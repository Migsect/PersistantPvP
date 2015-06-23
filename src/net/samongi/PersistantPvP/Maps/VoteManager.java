package net.samongi.PersistantPvP.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.samongi.SamongiLib.Maps.MapData;

public class VoteManager
{
  private final MapManager map_manager;
  private List<MapData> candidates = new ArrayList<>();
  private Map<UUID, String> votes = new HashMap<>();
  private Map<String, Double> vote_multiplier = new HashMap<>();
  private Map<UUID, Integer> vote_weight = new HashMap<>();
  
  public VoteManager(MapManager map_manager)
  {
    this.map_manager = map_manager;
  }
  
  
  public List<MapData> getCandidates(){return this.candidates;}
  public boolean isCandidate(MapData map){return this.isCandidate(map.getTag());}
  public boolean isCandidate(String map)
  {
    for(MapData m : candidates) if(m.getTag().equals(map)) return true;
    return false;
  }
  public void populateCandidates(int amount)
  {
    if(amount > map_manager.getMapKeys().size()) amount = map_manager.getMapKeys().size();
    List<String> map_keys = new ArrayList<>(map_manager.getMapKeys());
    List<MapData> new_candidates = new ArrayList<>();
    
    Random rand = new Random();
    for(int i = 0 ; i < amount ; i++)
    {
      int index = rand.nextInt(map_keys.size());
      new_candidates.add(map_manager.getMap(map_keys.get(index)));
      map_keys.remove(index);
    }
    this.candidates = new_candidates;
  }
  
  public void wipeVotes(){this.votes.clear();}
  public void wipeVote(Player player){this.wipeVote(player.getUniqueId());}
  public void wipeVote(UUID player){this.votes.remove(player);}
  public boolean setVote(Player player, String map){return this.setVote(player.getUniqueId(), map);}
  public boolean setVote(UUID player, String map)
  {
    if(!this.isCandidate(map)) return false;
    this.votes.put(player, map);
    return true;
  }
  public String getVote(Player player){return this.getVote(player.getUniqueId());}
  public String getVote(UUID player)
  {
    if(!this.votes.containsKey(player)) return null;
    return this.votes.get(player);
  }
  public int getVotes(MapData map){return this.getVotes(map.getTag());}
  public int getVotes(String map)
  {
    int sum = 0;
    for(UUID k :votes.keySet()) if(votes.get(k).equals(map)) sum++;  
    return sum;
  }
  public double getVoteMultiplier(MapData map){return this.getVoteMultiplier(map.getTag());}
  public double getVoteMultiplier(String map)
  {
    if(!this.vote_multiplier.containsKey(map)) return 1;
    return this.vote_multiplier.get(map);
  }
  public void setVoteMultiplier(MapData map, double value){this.vote_multiplier.put(map.getTag(), value);}
  public void setVoteMultiplier(String map, double value){this.vote_multiplier.put(map, value);}
  
  
  public void setVoteWeight(Player player, int weight){this.setVoteWeight(player.getUniqueId(), weight);}
  public void setVoteWeight(UUID player, int weight){this.vote_weight.put(player, weight);}
  public int getVoteWeight(Player player){return this.getVoteWeight(player.getUniqueId());}
  public int getVoteWeight(UUID player)
  {
    if(!this.vote_weight.containsKey(player)) return 1;
    else return this.vote_weight.get(player);
  }
}
