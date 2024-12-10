package teclas;

public enum Tecla {

	ENTER(new TeclaDef(66, 13, false)), BACKSPACE(new TeclaDef(67, 8, false)), DEL(new TeclaDef(112, 127, false)),
	CTRLIZQ(new TeclaDef(129, 0, false)), CTRLDER(new TeclaDef(130, 0, false)), WINDOWS(new TeclaDef(63, 0, false)),
	ALTIZQ(new TeclaDef(57, 0, false)), ALTDER(new TeclaDef(58, 0, false)), ESPACIO(new TeclaDef(62, 32, true)),
	ARRIBA(new TeclaDef(19, 0, false)), ABAJO(new TeclaDef(20, 0, false)), IZQUIERDA(new TeclaDef(21, 0, false)),
	DERECHA(new TeclaDef(22, 0, false)), SHIFTIZQ(new TeclaDef(59, 0, false)), SHIFTDER(new TeclaDef(60, 0, false)),
	TAB(new TeclaDef(61, 9, false)), ESCAPE(new TeclaDef(131, 27, false)), F1(new TeclaDef(244, 0, false)),
	F2(new TeclaDef(245, 0, false)), F3(new TeclaDef(246, 0, false)), F4(new TeclaDef(247, 0, false)),
	F5(new TeclaDef(248, 0, false)), F6(new TeclaDef(249, 0, false)), F7(new TeclaDef(250, 0, false)),
	F8(new TeclaDef(251, 0, false)), F9(new TeclaDef(252, 0, false)), F10(new TeclaDef(253, 0, false)),
	F11(new TeclaDef(254, 0, false)), F12(new TeclaDef(255, 0, false)), PRTSCR(new TeclaDef(-1, 0, false)),
	INSERT(new TeclaDef(133, 0, false)), PGUP(new TeclaDef(92, 0, false)), PGDN(new TeclaDef(93, 0, false)),
	HOME(new TeclaDef(3, 0, false)), END(new TeclaDef(132, 0, false)), NUMLOCK(new TeclaDef(78, 0, false)),
	q(new TeclaDef(45, 113, true)), w(new TeclaDef(51, 119, true)), e(new TeclaDef(33, 101, true)),
	r(new TeclaDef(46, 114, true)), t(new TeclaDef(48, 116, true)), y(new TeclaDef(53, 121, true)),
	u(new TeclaDef(49, 117, true)), i(new TeclaDef(37, 105, true)), o(new TeclaDef(43, 111, true)),
	p(new TeclaDef(44, 112, true)), a(new TeclaDef(29, 97, true)), s(new TeclaDef(47, 115, true)),
	d(new TeclaDef(32, 100, true)), f(new TeclaDef(34, 102, true)), g(new TeclaDef(35, 103, true)),
	h(new TeclaDef(36, 104, true)), j(new TeclaDef(38, 106, true)), k(new TeclaDef(39, 107, true)),
	l(new TeclaDef(40, 108, true)), z(new TeclaDef(54, 122, true)), x(new TeclaDef(52, 120, true)),
	c(new TeclaDef(31, 99, true)), v(new TeclaDef(50, 118, true)), b(new TeclaDef(30, 98, true)),
	n(new TeclaDef(42, 110, true)), m(new TeclaDef(41, 109, true)), Q(new TeclaDef(45, 81, true)),
	W(new TeclaDef(51, 87, true)), E(new TeclaDef(33, 69, true)), R(new TeclaDef(46, 82, true)),
	T(new TeclaDef(48, 84, true)), Y(new TeclaDef(53, 89, true)), U(new TeclaDef(49, 85, true)),
	I(new TeclaDef(37, 73, true)), O(new TeclaDef(43, 79, true)), P(new TeclaDef(44, 80, true)),
	A(new TeclaDef(29, 65, true)), S(new TeclaDef(47, 83, true)), D(new TeclaDef(32, 68, true)),
	F(new TeclaDef(34, 70, true)), G(new TeclaDef(35, 71, true)), H(new TeclaDef(36, 72, true)),
	J(new TeclaDef(38, 74, true)), K(new TeclaDef(39, 75, true)), L(new TeclaDef(40, 76, true)),
	Z(new TeclaDef(54, 90, true)), X(new TeclaDef(52, 88, true)), C(new TeclaDef(31, 67, true)),
	V(new TeclaDef(50, 86, true)), B(new TeclaDef(30, 66, true)), N(new TeclaDef(42, 78, true)),
	M(new TeclaDef(41, 77, true)), N1(new TeclaDef(81, 49, true)), N2(new TeclaDef(92, 50, true)),
	N3(new TeclaDef(103, 51, true)), N4(new TeclaDef(114, 52, true)), N5(new TeclaDef(125, 53, true)),
	N6(new TeclaDef(136, 54, true)), N7(new TeclaDef(147, 55, true)), N8(new TeclaDef(158, 56, true)),
	N9(new TeclaDef(169, 57, true)), N0(new TeclaDef(70, 48, true)), NUM1(new TeclaDef(1451, 49, true)),
	NUM2(new TeclaDef(1462, 50, true)), NUM3(new TeclaDef(1473, 51, true)), NUM4(new TeclaDef(1484, 52, true)),
	NUM5(new TeclaDef(1495, 53, true)), NUM6(new TeclaDef(1506, 54, true)), NUM7(new TeclaDef(1517, 55, true)),
	NUM8(new TeclaDef(1528, 56, true)), NUM9(new TeclaDef(1539, 57, true)), NUM0(new TeclaDef(1440, 48, true)),;

	private final TeclaDef tecla;

	Tecla(TeclaDef tecla) {
		this.tecla = tecla;
	}

	public TeclaDef getTecla() {
		return tecla;
	}

}
