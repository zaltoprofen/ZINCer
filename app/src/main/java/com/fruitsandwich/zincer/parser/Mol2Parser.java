package com.fruitsandwich.zincer.parser;

import com.google.common.collect.Lists;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Created by nakac on 15/07/23.
 */
public class Mol2Parser {
    public class ParseError extends RuntimeException {
        public ParseError(String message) {
            super(message);
        }

        public ParseError(Throwable cause) {
            super(cause);
        }
    }

    private enum Mode {
        Other,
        Atom,
        Bond
    }

    public Mol2 parse(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        List<Mol2.Atom> atoms = Lists.newArrayList();
        List<Mol2.Bond> bonds = Lists.newArrayList();
        Mode mode = Mode.Other;
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("@")) {
                    if (line.contains("ATOM"))
                        mode = Mode.Atom;
                    else if (line.contains("BOND"))
                        mode = Mode.Bond;
                    else
                        mode = Mode.Other;
                    continue;
                }
                switch (mode) {
                    case Atom:
                        atoms.add(procAtom(new Scanner(line)));
                        break;
                    case Bond:
                        bonds.add(procBond(new Scanner(line)));
                        break;
                }
            }
            return new Mol2(atoms, bonds);
        } catch (Throwable e) {
            throw new ParseError(e);
        }
    }

    private Mol2.Atom procAtom(Scanner scanner) {
        Integer id = scanner.nextInt();
        String name = scanner.next();
        float x = scanner.nextFloat();
        float y = scanner.nextFloat();
        float z = scanner.nextFloat();

        String typeStr = scanner.next();
        Mol2.AtomType type;
        switch (typeStr.charAt(0)) {
            case 'H':
                type = Mol2.AtomType.H;
                break;
            case 'O':
                type = Mol2.AtomType.O;
                break;
            case 'N':
                type = Mol2.AtomType.N;
                break;
            case 'S':
                type = Mol2.AtomType.S;
                break;
            case 'C':
                type = Mol2.AtomType.C;
                break;
            default:
                type = Mol2.AtomType.Unknown;
        }
        scanner.nextLine();
        return new Mol2.Atom(id, name, x, y, z, type);
    }

    private Mol2.Bond procBond(Scanner s) {
        Integer id = s.nextInt();
        Integer a1 = s.nextInt();
        Integer a2 = s.nextInt();
        s.nextLine();
        return new Mol2.Bond(id, a1, a2);
    }
}
