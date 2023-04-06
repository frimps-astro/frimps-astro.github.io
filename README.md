# Evaluating the Detection of Non-Overlapping Communities using the CDFR Algorithm
This repository is an unofficial implementation of the CDFR Algorithm, a proposed algorithm in the research work [Community Detection by Fuzzy Relations](https://ieeexplore.ieee.org/abstract/document/8031356)</br>
It is a project work to implement the algorithm for the purpose of replication of the experiments for evaluation and academic (see end of document for details) purposes.


## Abstract
> There are a lot of challenges many tasks face due to the high demand for knowledge from network data. In analyzing networks, finding their community structure is of topmost priority among the many issues faced. The selected paper employed fuzzy relations composition and a fuzzy-relation-based novel algorithm called Community Detection by Fuzzy Relations (CDFR) in studying network structures. To compute the fuzzy relation between the Nearest node with Greater Centrality (NGC) for individual nodes in a community was the goal of the CDFR algorithm. The NGC determined the community a node formed a part of. The construction of the decision graph was used to guide community detection. \\Non-overlapping community detection concept aimed at forming numerous communities from a network with each node belonging to exactly one community.

## Introduction
> Recognizing groups that possess similar features in a network is termed Community detection and such groups can be called communities. If an individual belongs to one and only one community, then we can say the communities do not overlap. In graph networks, nodes can be grouped according to the common features they share and a node belonging to one community could have a feature that can make it belong to another community. In that case, communities detected in the network would overlap i.e. a node may belong to more than one community. In the paper under evaluation, the focus is on non-overlapping communities.

## CDFR Approach
The `Nearest node with Greater Centrality (NGC)` determines which community any node in the graph would belong to. The NGC of each node denoted as `NGC(V)`, where `V` is a node in the graph, is determined and the `fuzzy relation`, `R(V, NGC(V))`, considered the dependency of `V` on `NGC(V)`, is computed. The degree centrality of a node (`deg(V)`) was considered too rough for the CDFR algorithm by the authors, so they used the term `centrality` as given in the equation below.
Since the CDFR algorithm is based on these two concepts; `NGC(V)` and `R(V, NGC(V))`, I give below the definitions of the parameters required for the computation of each:

$$ centrality(V) = deg(V) + \sum_{U\epsilon{\Gamma(V)}}\left(deg(U) + \sum_{W\epsilon{\Gamma(U)}}deg(W)\right)$$

`centrality(V)` here stands for the influence of the node V in the network and it was considered for the purpose of its simplicity and how well it aids in ranking the nodes. $\Gamma(n)$ represents the direct neighbors of node n and `deg(n)` represents the degree of node n. It considers a 2-hop neighbor nodes degree centralities.

The function `centralityOfNodes(G)` and `neighborNodesDegree(G, U)` computes `centrality(V)` using the formula above. Below is the code extract. The input parameters: a graph `G` and a neighbor node `U` and returns a list of each node and its centrality value. 

`baselineDegreeCentrality(G)` also accepts a graph `G` and return a dictionary of all nodes and their degree centralities (without considering neighbor nodes centralities) in the graph.

```python
def neighborNodesDegree(G, U):
     degU = G.degree(U) #degree of U

     for w in G.neighbors(U):
     degU += G.degree(w)

     return degU

def centralityOfNodes(G):
      centralities = dict()

      #find the centralities of all nodes
      for V in G.nodes():
          degV = G.degree(V) #degree of V

          for U in G.neighbors(V):
              degV += neighborNodesDegree(G, U)

          #save centrality of each V
          centralities[V] = degV

      return centralities

def baselineDegreeCentrality(G):
    baseline_centralities = dict()

    #find the degree centralities of all nodes
    for V in G.nodes():
      #save centrality of each V
      baseline_centralities[V] = G.degree(V)

    return baseline_centralities

```
`fuzzyPath`: This is a term I have come up for an important parameter required for finding the fuzzy relation. The fuzzy relation is defined as:

$$ R(V, NGC(V)) = \max_{p\epsilon{P}}\{ \mu_p(N_1, N_k) \} $$

where $\mu_p(N_1, N_k)$ represents the `fuzzyPath`, `P` is all paths that lead from a node `V` to `NGC(V)`, `p` is a path in the set of paths, and ${N_i}$ is a node in the path. `fuzzyPath` is defined by the formula:

$$ \mu_p(N_1, N_k) = \frac{1 + |\Gamma(N_i) \cap\Gamma(N_{i+1})|}{|\Gamma(N_i)|} $$

where $\Gamma(N)$ means neighbors of N and $\Gamma(N) \cap\Gamma(M)$ stands for common neighbors of node `N` and `M` i.e. the intersection of the neighbors of `N` and `M`.

The function `temporalFuzzyRelations(G, V)` accepts a graph `G` and a node `V` and returns a list of the fuzzy relations i.e. `fuzzyPaths` of all neighbors of `V`

``` python
def temporalFuzzyRelations(G, V):
    Xtemp = 0
    #store not visited nodes
    tempFuzzies = dict()
    
    #length of node v neighbors
    len_vn = len(sorted(G.neighbors(V))) 

    for X in G.neighbors(V):
        #common neighbors(cn) of both V and X
        vx_cn = nx.common_neighbors(G, V, X) 
        Xtemp = (1 + len(sorted(vx_cn))) /len_vn
        tempFuzzies[X] = Xtemp

    return tempFuzzies
```

The function `findNGCandFuzzyRelation(G, V, type='cdfr')` takes a graph `G`, a node `V`, and an optional parameter `type` that determines whether to use the baseline or cdfr algorithm and return the `NGC` node and `fuzzy relation` of node `V`.

``` python
def findNGCandFuzzyRelation(G, V, type="cdfr"):
    findtag = False
    W = V
    fuzzyrelation = 0
    closeTable = dict()
    
    #retrieve dictionary of node centralities
    if type == "baseline":
        nodeCentralities = baselineDegreeCentrality(G)
    else:
        nodeCentralities = centralityOfNodes(G)

    #retrieve dictionary of temporal fuzzy relations
    openTable = temporalFuzzyRelations(G, V)

    while len(openTable) != 0:
        #take node with maximum temporal fuzzy relation
        C = max(openTable.items(), key=operator.itemgetter(1))[0]

        #if we don't have an NGC
        if findtag == False:
            if nodeCentralities.get(C, 0) > nodeCentralities.get(V, 0):
                W = C
                fuzzyrelation = openTable.get(C)
                findtag = True
        else:
            if openTable.get(C) < fuzzyrelation:
                break

            if nodeCentralities.get(C, 0) > nodeCentralities.get(W, 0):
                W = C
                fuzzyrelation = openTable.get(C)
    
        closeTable[C] = openTable.get(C)

        #remove node and its value but return value -> default to return None to prevent KeyError
        CtmpFR = openTable.pop(C, None) 

        len_cn = len(sorted(G.neighbors(C))) #length of node c neighbors
        currentfr = 0

        for Y in G.neighbors(C):
            cy_cn = nx.common_neighbors(G, C, Y) #common neighbors(cn) of both C and Y
            currentfr = (1 + len(sorted(cy_cn))) / len_cn
            currentfr *= CtmpFR

            openTableContainsY = openTable.get(Y, None)
            closeTableContainsY = closeTable.get(Y, None)
            if openTableContainsY == None and closeTableContainsY == None:
                openTable[Y] = currentfr
            elif openTableContainsY != None:
                if currentfr > openTableContainsY:
                    openTable[Y] = currentfr
            elif closeTableContainsY != None:
                if currentfr > closeTableContainsY:
                    openTable[Y] = currentfr
                    closeTable.pop(Y, None)

    return W, fuzzyrelation
```
The function `constructCommunityStructure(G, delta, type="cdfr")` takes in a graph `G`, a threshold value `delta`, and an optional parameter `type` defaulted to the value cdfr and returns a list of recognized communities in the network. The `delta` value controls how many communities can be recognized and it is picked from the decision graphs constructed from the network.

``` python
def constructCommuityStructure(G, delta, type="cdfr"):
    #retrieve and sort nodes' centralities
    if type == "baseline":
        centralities = baselineDegreeCentrality(G)
    else:
        centralities = centralityOfNodes(G)

    centralities =dict(sorted(centralities.items(), key=lambda item: item[1], reverse=True))

    communities = dict()
    comnumber = -1
    
    for V in centralities:
        ngc, fr = findNGCandFuzzyRelation(G, V)
        if fr < delta:
            comnumber += 1
            communities[comnumber] = [V]
        else:
            #find the community number of ngc
            for comnum, com in communities.items():
                if ngc in com:
                    #add V to the community ngc of V belongs
                    communities[comnum].append(V)
                    break
            
    return communities

```

The function `draw(G, refinedData, datasetName)` visualizes the constructed communities in a graph using the [Netgraph](https://netgraph.readthedocs.io/en/stable/) library. It accepts a graph `G`, a `refinedData` from the function `nodeToCommunity(G, delta, type)` that returns a list of nodes and their associated communities, and a string `datasetName` which is the name of the dataset used.

The function `decisionGraphs(G, datasetName, type)` takes a graph `G`, a `datasetName`, and an optional parameter `type` and visualize a decision graph of the network.

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
- On Windows machines and some Apple machines (wihout silicon based chips), one can use [VS Code](https://code.visualstudio.com) to run this project

## Tweaking Code
Under `Dataset Setup and Visualization` in the `project_community_detection.ipynb`, you are free to change the decimal (delta/threshold) values  passed to the function `nodeToCommunity(G, 0.x, type)` to detect various communities in the networks. The third parameter type should only be passed as `baseline` if you want to test the baseline algorithm

Under `Constructing Decision Graphs`, run each dataset to construct its decision graph one at a time by commenting out the others. Running all at the same have effects on the results rendering some of them inaccurate.

**NB:** The number of communities allowed to be visualized is limited to 20 i.e. the number of colors (used to identify various detected communities) available to the variable `community_to_color` under `Util Functions -> draw(G, refinedData, datasetName)`. In the case of the baseline experiments, you may encounter a spike in the number of detected communities under the football dataset, which wouldn't be visualized. An exception is thrown with a message telling you the number of communities that were detected.

**The resulting graphs of your experiments are saved under the directory `graphs/`**

## Results
The results of my experiment are saved to the directory `results/` in the repository. `communities/` contains recognized communities as I played with the delta values and `decisions/`.

Visit <a href="https://frimps-astro.github.io/community-detection" target="_blank">Community Detection</a> to see a gallery of a few of the graph results from my experiments.

### Code Demo
<a href="https://player.vimeo.com/video/813632413?h=e8c34b2b94" title="Link Title"><img src="https://player.vimeo.com/video/813632413?h=e8c34b2b94" alt="CDFR CODE DEMO" /></a>


## Course Information
```
Code: COSC 5P30
Course Name: Graph Data Mining
Description: This class is a graduate course which investigates knowledge representation & reasoning and MLtechniques to create integrated intelligent systems centered around knowledge graphs (KG). 

Student: Clement Frimpong, Computer Science
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