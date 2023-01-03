package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

import jacenre.dbla.AbstractSolver;
import jacenre.dbla.BottomUpSolver;
import jacenre.dbla.CompoundSolver;
import jacenre.dbla.FirstFitSolver;
import jacenre.dbla.GeneticSolver;
import jacenre.dbla.SkylineSolver;
import jacenre.dbla.TopLeftSolver;

/**
 * Testing class using {@link CompoundSolver}
 */
@DisplayName("Simple Compound Solver")
public class CompoundSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        CompoundSolver compoundSolver = new CompoundSolver();
        compoundSolver.addSolver(new FirstFitSolver());
        compoundSolver.addSolver(new SkylineSolver());
        compoundSolver.addSolver(new GeneticSolver(new TopLeftSolver(false), true));
//        compoundSolver.addSolver(new TopLeftSolver());
        compoundSolver.addSolver(new BottomUpSolver());
//        compoundSolver.addSolver(new CompressionSolver());
//        compoundSolver.addSolver(new ReverseFitSolver());
//        compoundSolver.addSolver(new SimpleTopLeftSolver());

        return compoundSolver;
    }
}
