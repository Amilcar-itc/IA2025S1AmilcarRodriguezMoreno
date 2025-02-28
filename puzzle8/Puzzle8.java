package inteligenciaArtificial;

import java.util.PriorityQueue;
import java.util.Stack;

public class Puzzle8 { // Amilcar Rodriguez Moreno
	// Generador aleatorio:
	// https://sliding.toys/mystic-square/8-puzzle/
	// Resolvedor en linea:
	// https://8puzzlesolver.com/#step3
	public static void main(String[]args) {
		// System.out.println("ola");
		byte[][] game2x2 = { // Sin solucion
			    {3, 2},
			    {1, 0}
			};
		byte[][] game3x3 = { // Sin solucion
			    {1, 2, 3},
			    {4, 5, 6},
			    {8, 7, 0}
			};/**/
		byte[][] game6 = { //
			    {4, 1, 3},
			    {7, 2, 5},
			    {0, 8, 6}
			};/**/
		byte[][] game4x4 = {
			    {0, 2, 3, 4}, //
			    {1, 5, 7, 8},
			    {9, 6, 10, 12},
			    {13, 14, 11, 15}
			};/**/
		byte[][] game3 = { //
			    {13, 11, 3, 15},
			    {0, 5, 1, 9},
			    {12, 8, 4, 14},
			    {10, 6, 2, 7}
			};
		
		byte[][] game15 = { //
			    {4, 3, 1},
			    {5, 6, 8},
			    {7, 0, 2}
			};
		byte[][] game20 = { //
			    {3, 1, 0},
			    {4, 6, 8},
			    {5, 7, 2}
			};
		byte[][] game25 = { //
			    {2, 8, 7},
			    {3, 6, 4},
			    {5, 0, 1}
			};
		byte[][] game31 = { //
			    {8, 6, 7},
			    {2, 5, 4},
			    {3, 0, 1}
			};
		byte[][] gameX = { //
			    {1, 0, 6},
			    {7, 8, 4},
			    {5, 2, 3}
			};
		
		
		PuzzleNode test = new PuzzleNode(game4x4, 0);
		test.print();
		
		PuzzleXSolver solver = new PuzzleXSolver();
		solver.setGame(test);
		solver.setDebug(!true);
		long time = System.currentTimeMillis();
		
		// widthSolve
		// depthSolve
		// limitDepthSolve
		// iterativeDepthSolve
		// priorityWidthSolve
		// priorityDepthSolve
		// priorityLimitDepthSolve
		// priorityIterativeDepthSolve
		// priorityQueueSolve
		
		int memory = solver.priorityQueueSolve();
		time = System.currentTimeMillis()-time;
		System.out.println("Tiempo de ejecucion: "+time+" milisegundos"
				+ "\nMemoria usada: "+memory);
		System.out.println("Coste de solucion: "+(solver.getSolutionPath().length-1));
		solver.printSolutionPath();
	}
}

class PuzzleXSolver {
	private PuzzleNode root, solution;
	private byte[][] target;
	private boolean debug;
	PuzzleXSolver() {
		clean();
		debug = false;
	}
	
	int widthSolve() {
		solution = null;
		
		PuzzleNode finalPointer = root, actualNode = root;
		int depth = 0, possibleCases = 1, total = 1;
		if (actualNode.equals(target)) {
			solution = actualNode;
			printDebug("Solucion encontrada\nProfundidad: "+depth);
			return total;
		}
		while (actualNode != null) {
			PuzzleNode[] successors = successors(actualNode);
			for (PuzzleNode successor : successors) { // recorrer hijos de nodos
				if (!simpleExists(successor)) {
					successor.setParent(actualNode);
					finalPointer.setNext(successor);
					finalPointer = successor;
					/// Controlar crecimiento
					if (depth < successor.getDepth()) {
						printDebug("Profundidad: "+depth+
								" | Casos analizados: "+possibleCases+" | total: "+total);
						possibleCases = 0;
					}
					depth = successor.getDepth();
					possibleCases++;
					total++;
					if (successor.equals(target)) {
						solution = successor;
						printDebug("Solucion encontrada\nProfundidad: "+successor.getDepth()+
								" | Casos posibles: "+possibleCases+" | total: "+total);
						return simpleCount();
					}
				}
			}
			
			actualNode = actualNode.getNext();
		}
		printDebug("Sin solucion posible");
		return simpleCount();
	}
	
	int depthSolve() {
		return limitDepthSolve(Integer.MAX_VALUE);
	}
	
	int iterativeDepthSolve() {
		solution = null;
		boolean deb = debug;
		int max = 0;
		for (int i=0; solution==null && i<=31; i++) {
			printDebug("Incrementando profundidad limite a "+i);
			debug = false;
			int aux = limitDepthSolve(i);
			if (max < aux)
				max = aux;
			debug = deb;
		}
		return max;
	}
	int limitDepthSolve() {
		return limitDepthSolve(31);
	}
	int limitDepthSolve(int limitDepth) {
		int max = 0;
		solution = null; root.successors1 = null;
		Stack<PuzzleNode> searchPath = new Stack<>();
		searchPath.add(root);
		while (!searchPath.isEmpty()) {
			if (max<searchPath.size()) {
				max = searchPath.size();
				printDebug("Nueva profundidad maxima alcanzada: "+max);
			}
			PuzzleNode actualNode = searchPath.peek();
			printDebug("Entrando a nodo profundidad: "+actualNode.getDepth());
			//if (debug) actualNode.print();
			if (actualNode.equals(target)) {
				solution = actualNode; printDebug("solucion encontrada");
				return max;
			} else if (actualNode.getDepth() >= limitDepth) {
				searchPath.pop();
				printDebug("profundidad limite alcanzada ("+limitDepth+")");
			} else if (actualNode.successors1 == null) {
				if (ancestryExists(actualNode)) {
					searchPath.pop();
					printDebug("existe");
				} else {
					PuzzleNode[] successors = successors(actualNode);
					printDebug("no existe");
					actualNode.successors1 = new Stack<>();
					for (PuzzleNode successor : successors) {
						successor.setParent(actualNode);
						actualNode.successors1.add(successor);
					}
					searchPath.add(actualNode.successors1.pop());
				}
			} else if (actualNode.successors1.isEmpty()) {
				searchPath.pop();
				printDebug("con hijos invalidos");
			} else {
				searchPath.add(actualNode.successors1.pop());
				printDebug("siguiente hijo");
			}
		}
		printDebug("Sin solucion posible");
		return max;
	}
	
	int priorityWidthSolve() {
		int max=0;
		solution = null;
		PriorityQueue<PuzzleNode> actualQueue = new PriorityQueue<>(),
				nextQueue = new PriorityQueue<>();
		PuzzleNode actualNode = root, finalPointer = actualNode;
		int depth = 0, total = 0;
		if (actualNode.equals(target)) {
			solution = actualNode;
			printDebug("Solucion encontrada\nProfundidad: "+depth);
			return max;
		}
		while (actualNode != null) {
			PuzzleNode[] successors = successors(actualNode);
			if (max<(actualQueue.size()+nextQueue.size()))
				max = actualQueue.size()+nextQueue.size();
			for (PuzzleNode successor : successors) { // recorrer hijos de nodos
				if (!simpleExists(successor)) {//successor.print();
					successor.setPriority(successor.manhattanDistanceFrom(target));
					successor.setParent(actualNode);
					finalPointer.setNext(successor);
					finalPointer = successor;
					nextQueue.add(successor);
					
					/// Controlar crecimiento
					int leftingCases = actualQueue.size()+nextQueue.size();
					if (depth < successor.getDepth())
						printDebug("Profundidad: "+depth+
								" | Casos en memoria: "+leftingCases+" | total casos: "+total);
					depth = successor.getDepth();
					total++;
					if (successor.equals(target)) {
						solution = successor;
						printDebug("Solucion encontrada\nProfundidad: "+successor.getDepth()+
								" | Casos restantes: "+leftingCases+" | total: "+total);
						return simpleCount();
					}
				}
			}
			if (actualQueue.size() == 0) {
				actualQueue = nextQueue;
				nextQueue = new PriorityQueue<>();
			}
			actualNode = actualQueue.poll();
		}
		printDebug("Sin solucion posible");
		
		return simpleCount();
	}
	
	int priorityQueueSolve() {
		solution = null;
		PriorityQueue<PuzzleNode> queue = new PriorityQueue<>();
		PuzzleNode finalPointer = root;
		queue.add(finalPointer);
		int maxQueueSize = 0, maxDepth = 0;
		while (!queue.isEmpty()) {
			PuzzleNode actualNode = queue.poll();
			if (actualNode.equals(target)) {
				solution = actualNode;
				int total = simpleCount();
				printDebug("Solucion encontrada\nProfundidad: "+actualNode.getDepth()+
						" | Memoria máxima usada: "+total);
				return total;
			}
			PuzzleNode[] successors = successors(actualNode);
			for (PuzzleNode successor : successors) { // recorrer hijos de nodos
				if (!simpleExists(successor)) {//successor.print();
					successor.setPriority(successor.manhattanDistanceFrom(target)+successor.getDepth());
					successor.setParent(actualNode);
					finalPointer.setNext(successor);
					finalPointer = successor;
					queue.add(successor);
					/// Controlar crecimiento
					if (maxDepth < successor.getDepth()) {
						maxDepth = successor.getDepth();
						printDebug("Profundidad: "+maxDepth+
							" | Casos en memoria: "+maxQueueSize);
					}
					if (maxQueueSize < queue.size())
						maxQueueSize = queue.size();
				}
			}
		}
		printDebug("Sin solucion posible");
		return simpleCount();
	}
	
	int priorityDepthSolve() {
		return priorityLimitDepthSolve(Integer.MAX_VALUE);
	}
	int priorityIterativeDepthSolve() {
		solution = null;
		boolean deb = debug;
		int max = 0;
		for (int i=0; solution==null && i<=31; i++) {
			printDebug("Incrementando profundidad limite a "+i);
			debug = false;
			int aux = priorityLimitDepthSolve(i);
			if (max < aux)
				max = aux;
			debug = deb;
		}
		return max;
	}
	int priorityLimitDepthSolve() {
		return priorityLimitDepthSolve(31);
	}
	int priorityLimitDepthSolve(int limitDepth) {
		int max = 0;
		solution = null; root.successors2 = null;
		Stack<PuzzleNode> searchPath = new Stack<>();
		searchPath.add(root);
		while (!searchPath.isEmpty()) {
			if (max<searchPath.size()) {
				max = searchPath.size();
				printDebug("Nueva profundidad maxima alcanzada: "+max);
			}
			PuzzleNode actualNode = searchPath.peek();
			printDebug("Entrando a nodo profundidad: "+actualNode.getDepth());
			if (debug) actualNode.print();
			if (actualNode.equals(target)) {
				solution = actualNode; printDebug("solucion encontrada");
				return max;
			} else if (actualNode.getDepth() >= limitDepth) {
				searchPath.pop();
				printDebug("profundidad limite alcanzada ("+limitDepth+")");
			} else if (actualNode.successors2 == null) {
				if (ancestryExists(actualNode)) {
					searchPath.pop();
					printDebug("existe");
				} else {
					PuzzleNode[] successors = successors(actualNode);
					printDebug("no existe");
					actualNode.successors2 = new PriorityQueue<>();
					for (PuzzleNode successor : successors) {
						successor.setPriority(successor.manhattanDistanceFrom(target));
						successor.setParent(actualNode);
						actualNode.successors2.add(successor);
					}
					searchPath.add(actualNode.successors2.poll());
				}
			} else if (actualNode.successors2.isEmpty()) {
				searchPath.pop();
				printDebug("con hijos invalidos");
			} else {
				searchPath.add(actualNode.successors2.poll());
				printDebug("siguiente hijo");
			}
		}
		printDebug("Sin solucion posible");
		return max;
	}
	private int simpleCount() {
		int total = 0;
		for (PuzzleNode node=root; node!=null; node=node.getNext())
			total++;
		return total;
	}
	private boolean simpleExists(PuzzleNode state) {
		for (PuzzleNode node=root; node!=null; node=node.getNext())
			if (node.equals(state))
				return true;
		return false;
	}
	private boolean ancestryExists(PuzzleNode state) {
		for (PuzzleNode node=state.getParent(); node!=null; node=node.getParent())
			if (node.equals(state))
				return true;
		return false;
	}
	
	PuzzleNode[] successors(PuzzleNode state) {
		PuzzleNode[] successors;
		int XaxisSize = state.getBoard().length;
		int YaxisSize = state.getBoard()[0].length;
		int numSuccessors = 0;
		byte[][] north = null, south = null, east = null, west = null;
		
		// Buscar la ubicacion del espacio (numero 0)
		boolean flag = false;
		int x = -1, y = -1;
		for (int i = 0; i < XaxisSize && !flag; i++)
			for (int j = 0; j < YaxisSize && !flag; j++)
				if (state.getBoard()[i][j] == 0) {
					x = i; y = j; flag = true;
				}
		//System.out.println(x+","+y);
		if (x-1>=0) {
			numSuccessors++;
			north = swap(state.getBoard(),x,y,x-1,y);
		}
		if (x+1<XaxisSize) {
			numSuccessors++;
			south = swap(state.getBoard(),x,y,x+1,y);
		}
		if (y-1>=0) {
			numSuccessors++;
			west = swap(state.getBoard(),x,y,x,y-1);
		}
		if (y+1<YaxisSize) {
			numSuccessors++;
			east = swap(state.getBoard(),x,y,x,y+1);
		}
		successors = new PuzzleNode[numSuccessors];
		int i=0;
		if (north != null) {
			successors[i] = new PuzzleNode(north, state.getDepth()+1);
			i++;
		}
		if (west != null) {
			successors[i] = new PuzzleNode(west, state.getDepth()+1);
			i++;
		}
		if (east != null) {
			successors[i] = new PuzzleNode(east, state.getDepth()+1);
			i++;
		}
		if (south != null) {
			successors[i] = new PuzzleNode(south, state.getDepth()+1);
			i++;
		}
		return successors;
	}
	
	private byte[][] swap(byte[][] board, int x1, int y1, int x2, int y2) {
		byte[][] copy = new byte[board.length][]; 
		for (int i = 0; i < board.length; i++)
			copy[i] = board[i].clone();
		byte aux = copy[x1][y1];
		copy[x1][y1] = copy[x2][y2];
		copy[x2][y2] = aux;
		return copy;
	}
	
	PuzzleNode[] getSolutionPath() {
		if (solution == null)
			return null;
		PuzzleNode pivot = solution;
		PuzzleNode[] path = new PuzzleNode[] {pivot};
		while (pivot.getParent() != null) {
			pivot = pivot.getParent();
			PuzzleNode[] aux = path.clone();
			path = new PuzzleNode[aux.length+1];
			path[0] = pivot;
			for(int i=0; i<aux.length; i++)
				path[i+1] = aux[i];
		}
		return path;
	}
	
	void printSolutionPath() {
		PuzzleNode[] path = getSolutionPath();
		if (path != null)
			for (int i=0;i<path.length;i++) {
				System.out.println("sucesion "+i);
				path[i].print();
			}
		else
			System.out.println("Sin solucion");
	}

	private void clean() {
		root = null;
		solution = null;
		target = null;
	}
	
	void setGame(byte[][] initialState) {
		// validar matriz: rectangular
		// validar numeros: secuenciales, no repetidos, hay un 0, no negativos
		
		// encontrar el numero menor
		int rows = initialState.length, cols = initialState[0].length;
		byte minor = Byte.MAX_VALUE;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				if (initialState[i][j] != 0 && initialState[i][j] < minor)
					minor = initialState[i][j];
		// crear version resuelta
		byte[][] solution = new byte[rows][cols];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				solution[i][j] = minor++;
		solution[rows - 1][cols - 1] = 0;
		setGame(initialState, solution);
	}
	void setGame(PuzzleNode initialState) {
		setGame(initialState.getBoard());
	}
	void setGame(byte[][] initialState,byte[][] targetState) {
		// validar matrices: rectangular (ambas), del mismo tamaño
		// validar numeros: concuerdan ambas matrices, no negativos
		clean();
		
		root = new PuzzleNode(initialState,0);
		byte[][] copy = new byte[targetState.length][];
	    for (int i = 0; i < targetState.length; i++)
	    	copy[i] = targetState[i].clone();
		this.target = copy;
		//simpleSolve();
	}
	void setGame(PuzzleNode initialState,PuzzleNode targetState) {
		setGame(initialState.getBoard(),targetState.getBoard());
	}
	void setGame(byte[][] initialState,PuzzleNode targetState) {
		setGame(initialState,targetState.getBoard());
	}
	void setGame(PuzzleNode initialState,byte[][] targetState) {
		setGame(initialState.getBoard(),targetState);
	}
	void setDebug(boolean debug) {
		this.debug = debug;
	}
	private void printDebug(String text) {
		if (debug)
			System.out.println(text);
	}
}

class PuzzleNode implements Comparable<PuzzleNode> {
	private byte[][] board;
	private int depth, priority;
	private PuzzleNode parent, next;
	
	Stack<PuzzleNode> successors1;
	PriorityQueue<PuzzleNode> successors2;
	
	PuzzleNode(byte[][] board, int depth){
		this.board = new byte[board.length][];
		for (int i = 0; i < board.length; i++)
			this.board[i] = board[i].clone();
		this.depth = depth;
		parent = null;
		next = null;
		priority = 0;
		
		successors1 = null;
		successors2 = null;
	}
	boolean equals(PuzzleNode node) {
		if (node == null) return false;
		return equals(node.getBoard());
	}
	boolean equals(byte[][] node) {
		if (node == null) return false;
		// Verificar dimensiones
		if (board.length != node.length|| board[0].length != node[0].length)
			return false;
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++)
				if (board[i][j] != node[i][j])
					return false;
		return true;
	}
	public int manhattanDistanceFrom(PuzzleNode comparison) {
		return manhattanDistanceFrom(comparison.getBoard());
	}
	public int manhattanDistanceFrom(byte[][] comparedBoard) {
		int sum = 0;
		for (int i=0; i<board.length;i++)
			for (int j=0; j<board[i].length; j++) {
				byte num = board[i][j];
				if (num!=0)
					for (int x = 0; x < comparedBoard.length; x++)
						for (int y = 0; y < comparedBoard[x].length; y++)
							if (comparedBoard[x][y] == num)
								sum += Math.abs(i - x) + Math.abs(j - y);
			}
		return sum;
	}
	public int compareTo(PuzzleNode node) {
		return Integer.compare(this.priority, node.getPriority());
	}
	void print() {
		int XaxisSize = board.length;
		int YaxisSize = board[0].length;
		String head = "+";
		for (int i=0; i<YaxisSize; i++)
			head += "-----+";
		System.out.println(head);
		for (int x=0; x<XaxisSize; x++) {
			String row = "|";
			for (int y=0; y<YaxisSize; y++)
				row += String.format(" %-3d |", board[x][y]);
			System.out.println(row+"\n"+head);
		}
	}
	byte[][] getBoard() {
		byte[][] copy = new byte[board.length][];
	    for (int i = 0; i < board.length; i++)
	    	copy[i] = board[i].clone();
	    return copy;
	}
	int getDepth() {
		return depth;
	}
	void setParent(PuzzleNode newParent) {
		this.parent = newParent;
	}
	PuzzleNode getParent() {
		return parent;
	}
	void setNext(PuzzleNode next) {
		this.next = next;
	}
	PuzzleNode getNext() {
		return next;
	}
	void setPriority(int priority ) {
		this.priority = priority;
	}
	int getPriority() {
		return priority;
	}
}
