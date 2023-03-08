# Evaluating the Detection of Non-Overlapping Communities using the CDFR Algorithm
This repository is an unofficial implementation of the CDFR Algorithm, a proposed algorithm in the research work [Community Detection by Fuzzy Relations](https://ieeexplore.ieee.org/abstract/document/8031356)</br>
It is a project work to implement the algorithm for the purpose of replication of the experiments for evaluation purposes.


## Abstract
> There are a lot of challenges many tasks face due to the high demand for knowledge from network data. In analyzing networks, finding their community structure is of topmost priority among the many issues faced. The selected paper employed fuzzy relations composition and a fuzzy-relation-based novel algorithm called Community Detection by Fuzzy Relations (CDFR) in studying network structures. To compute the fuzzy relation between the Nearest node with Greater Centrality (NGC) for individual nodes in a community was the goal of the CDFR algorithm. The NGC determined the community a node formed a part of. The construction of the decision graph was used to guide community detection. \\Non-overlapping community detection concept aimed at forming numerous communities from a network with each node belonging to exactly one community.

## Introduction
> Recognizing groups that possess similar features in a network is termed Community detection and such groups can be called communities. If an individual belongs to one and only one community, then we can say the communities do not overlap. In graph networks, nodes can be grouped according to the common features they share and a node belonging to one community could have a feature that can make it belong to another community. In that case, communities detected in the network would overlap i.e. a node may belong to more than one community. In the paper under evaluation, the focus is on non-overlapping communities.

## CDFR Methodology
The `Nearest node with Greater Centrality (NGC)` is used to determine which community any node in the graph would belong to. The NGC of each node denoted as `NGC(V)`, where `V` is a node in the graph, is determined and the `fuzzy relation`, `R(V, NGC(V))`, considered the dependency of `V` on `NGC(V)`, is computed. The degree centrality of a node (`deg(V)`) was considered too rough for the CDFR algorithm by the authors so they used the term `centrality` as specified ahead.
Since the CDFR algorithm is based on these two concepts; `NGC(V)` and `R(V, NGC(V))`, I will defined the parameters required for the computation of each:

$$ centrality(V) = deg(V) + \sum_{U\epsilon{\Gamma(V)}}\left(deg(U) + \sum_{W\epsilon{\Gamma(U)}}deg(W)\right)$$

`centrality(V)` here stands for the influence of the node V in the network and it was considered for the purpose of its simplicity and how well it aids in ranking the nodes. $\Gamma(n)$ represents the direct neighbors of node n and `deg(n)` represents the degree of node n. 

The function `centralityOfNodes(G)` and `neighborNodesDegree(G, U)` computes `centrality(V)` using the formula above. Below is the code extra. The input parameters: a graph `G` and a neighbor node `U` and returns a list of each node and its centrality value. 
```python
1   def neighborNodesDegree(G, U):
2      degU = G.degree(U) #degree of U
3
4      for w in G.neighbors(U):
5      degU += G.degree(w)
6
7      return degU
8
9   def centralityOfNodes(G):
10      centralities = dict()
11
12      #find the centralities of all nodes
13      for V in G.nodes():
14          degV = G.degree(V) #degree of V
15
16          for U in G.neighbors(V):
17              degV += neighborNodesDegree(G, U)
18
19          #save centrality of each V
20          centralities[V] = degV
21
22      return centralities

```
`fuzzyPath`: This is a term I have come up for an important parameter required for finding the fuzzy relation. The fuzzy relation is defined as:

$$ R(V, NGC(V)) = \max_{p\epsilon{P}}\{ \mu_p(N_1, N_k) \} $$

where $\mu_p(N_1, N_k)$ represents the `fuzzyPath`, `P` is all paths that lead from a node `V` to `NGC(V)`, `p` is a path in the set of paths, and ${N_i}$ is a node in the path. `fuzzyPath` is defined by the formula:

$$ \mu_p(N_1, N_k) = \frac{1 + |\Gamma(N_i) \cap\Gamma(N_{i+1})|}{|\Gamma(N_i)|} $$

where $\Gamma(N) \cap\Gamma(M)$ stands for common neighbors of node `N` and `M` i.e. the intersection of the neighbors of `N` and `M`.

The function `temporalFuzzyRelations(G, V)` accepts a graph `G` and a node `V` and returns a list of the fuzzy relations i.e. `fuzzyPaths` of all neighbors of `V`

``` python
1   def temporalFuzzyRelations(G, V):
2       Xtemp = 0
3       #store not visited nodes
4       tempFuzzies = dict()
5       
        #length of node v neighbors
6       len_vn = len(sorted(G.neighbors(V))) 
7
8       for X in G.neighbors(V):
9            #common neighbors(cn) of both V and X
10           vx_cn = nx.common_neighbors(G, V, X) 
11           Xtemp = (1 + len(sorted(vx_cn))) /len_vn
12           tempFuzzies[X] = Xtemp
13
14      return tempFuzzies
```

The function `findNGCandFuzzyRelation(G, V)` takes a graph `G` and a node `V` and return the `NGC` node and `fuzzy relation` of node `V`.

``` python
1   def findNGCandFuzzyRelation(G, V):
2       findtag = False
3       W = V
4       fuzzyrelation = 0
5       closeTable = dict()
6    
7       #retrieve dictionary of node centralities
8       nodeCentralities = centralityOfNodes(G) 
9
10      #retrieve dictionary of temporal fuzzy relations
11      openTable = temporalFuzzyRelations(G, V)
12
13      while len(openTable) != 0:
14          #take node with maximum temporal fuzzy relation
15          C = max(openTable.items(), key=operator.itemgetter(1))[0]
16
17          #if we don't have an NGC
18          if findtag == False:
19              if nodeCentralities.get(C, 0) > nodeCentralities.get(V, 0):
20                  W = C
21                  fuzzyrelation = openTable.get(C)
22                  findtag = True
23          else:
24              if openTable.get(C) < fuzzyrelation:
25                  break
26
27              if nodeCentralities.get(C, 0) > nodeCentralities.get(W, 0):
28                  W = C
29                  fuzzyrelation = openTable.get(C)
30    
31          closeTable[C] = openTable.get(C)
32
33          #remove node and its value but return value -> default to return None to prevent KeyError
34          CtmpFR = openTable.pop(C, None) 
35
36          len_cn = len(sorted(G.neighbors(C))) #length of node c neighbors
37          currentfr = 0
38
39          for Y in G.neighbors(C):
40              cy_cn = nx.common_neighbors(G, C, Y) #common neighbors(cn) of both C and Y
41              currentfr = (1 + len(sorted(cy_cn))) / len_cn
42              currentfr *= CtmpFR
43
44              openTableContainsY = openTable.get(Y, None)
45              closeTableContainsY = closeTable.get(Y, None)
46              if openTableContainsY == None and closeTableContainsY == None:
47                  openTable[Y] = currentfr
48              elif openTableContainsY != None:
49                  if currentfr > openTableContainsY:
50                      openTable[Y] = currentfr
51              elif closeTableContainsY != None:
52                  if currentfr > closeTableContainsY:
53                      openTable[Y] = currentfr
54                      closeTable.pop(Y, None)
55
56      return W, fuzzyrelation
```
The function `constructCommunityStructure(G, delta)` takes in a graph `G` and a threshold value `delta` and returns a list of recognized communities in the network. The `delta` value controls how many communities can be recognized and it is picked from the decision graphs constructed from the network.

``` python
1   def constructCommuityStructure(G, delta):
2          #retrieve and sort nodes’ centralities
3          centralities = centralityOfNodes(G)
4          centralities =dict(sorted(centralities.items(),key=lambda item: item[1], reverse=True))
5
6          communities = dict()
7          comnumber = -1
8
9          for V in centralities:
10              ngc, fr = findNGCandFuzzyRelation(G, V)
11              if fr < delta:
12                  comnumber += 1
13                  communities[comnumber] = [V]
14              else:
15                  #find the community number of ngc
16                  for comnum, com in communities.items():
17                      if ngc in com:
18                          #add V to the community ngc of V belongs
19                          communities[comnum].append(V)
20                          break
21
22          return communities
```

The function `draw(G, refinedData, datasetName)` visualizes the constructed communities in a graph using the [Netgraph](https://netgraph.readthedocs.io/en/stable/) library. It accepts a graph `G`, a `refinedData` from the function `nodeToCommunity(G, delta)` that returns a list of nodes and their associated communities, and a string `datasetName` which is the name of the dataset used.

The function `decisionGraphs(G, datasetName)` takes a graph `G` and a `datasetName` and visualize a decision graph of the network.

## Datasets
The network datasets used were:
- [Karate Club](https://networkx.org/documentation/stable/auto_examples/graph/plot_karate_club.html)
- [Football](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/football.gml)
- [US Politics Books](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/us_politics_books.gml)
- [Dolphins](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/dolphins.gml)
- Facebook with [414](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/facebook414.edges) and [686](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/facebook686.edges) edges
- [Twitter](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/twitter.edges)
- [Google Plus](https://github.com/frimps-astro/frimps-astro.github.io/blob/community/datasets/googleplus.gml)

All these datasets can be found in the directory `datasets/` in the repository.

## Environment
The essential packages required to run the code are:
- python3 (>=3.11.2)
- [netgraph](https://netgraph.readthedocs.io/en/latest/)
- [networkx](https://networkx.org/documentation/stable/reference/index.html)
- [matploblib ](https://pypi.org/project/matplotlib/)
- [Jupyter](https://jupyter.org/install) Notebook or [Anaconda](https://docs.anaconda.com/anaconda/install/index.html) 

## Setup
- Download/clone repository
- Open project_community_detection.ipynb file using Jupyter or via Anaconda
- On Windows machines and some Apple machines, one can use [VS Code](https://code.visualstudio.com) to run this project

## Tweaking Code
Under `Dataset Setup and Visualization` in the `project_community_detection.ipynb`, you are free to change the decimal (delta/threshold) values  passed to the function `nodeToCommunity(G, 0.x)` to detect various communities in the networks.

Under `Constructing Decision Graphs`, run each dataset to construct its decision graph one at a time by commenting out the others. Running all at the same have effects on the results rendering some of them inaccurate.

**NB:** The number of communities allowed to be visualized is limited to 20 i.e. the number of colors (used to identify various detected communities) available to the variable `community_to_color` under `Util Functions -> draw(G, refinedData, datasetName)`.

**The resulting graphs of your experiments are saved under the directory `graphs/`**

## Results
The results of my experiment are saved to the directory `results/` in the repository. `communities/` contains recognized communities as I played with the delta values and `decisions/`.

Visit <a href="https://frimps-astro.github.io/community-detection" target="_blank">Community Detection</a> to see a gallery of a few of the graph results from my experiments.

## Course Information
```
Code: COSC 5P30
Course Name: Graph Data Mining
Description: This class is a graduate course which investigates knowledge representation & reasoning and MLtechniques to create integrated intelligent systems centered around knowledge graphs (KG). 

Instructor: Professor Renata Dividino, Computer Science
Institution: Brock University
```

## Citation
```
@ARTICLE{8031356,
  author={Luo, Wenjian and Yan, Zhenglong and Bu, Chenyang and Zhang, Daofu},
  journal={IEEE Transactions on Emerging Topics in Computing}, 
  title={Community Detection by Fuzzy Relations}, 
  year={2020},
  volume={8},
  number={2},
  pages={478-492},
  doi={10.1109/TETC.2017.2751101}
}
```