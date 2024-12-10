package teclas;

import com.badlogic.gdx.Input.Keys;

public enum Letras {
	A("a","A", Keys.A),
	B("b","B", Keys.B),
	C("c","C", Keys.C),
	D("d","D", Keys.D),
	E("e","E", Keys.E),
	F("f","F", Keys.F),
	G("g","G", Keys.G),
	H("h","H", Keys.H),
	I("i","I", Keys.I),
	J("j","J", Keys.J),
	K("k","K", Keys.K),
	L("l","L", Keys.L),
	M("m","M", Keys.M),
	N("n","N", Keys.N),
	O("o","O", Keys.O),
	P("p","P", Keys.P),
	Q("q","Q", Keys.Q),
	R("r","R", Keys.R),
	S("s","S", Keys.S),
	T("t","T", Keys.T),
	U("u","U", Keys.U),
	V("v","V", Keys.V),
	W("w","W", Keys.W),
	X("x","X", Keys.X),
	Y("y","Y", Keys.Y),
	Z("z","Z", Keys.Z);
	
	private String min, may;
	private int key;
	
	Letras(String min, String may, int key){
		this.min = min;
		this.may = may;
		this.key = key;
	}

	public int getKey() {
		return this.key;
	}
	
	public String getMin() {
		return this.min;
	}

	public String getMay() {
		return this.may;
	}
	
}
