package fm.mox.spikes.functionaljava.scalacheck;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import fj.F;
import fj.P;
import fj.P1;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.test.Gen;
import fj.test.Property;
import fj.test.Shrink;
import fm.mox.spikes.functionaljava.scalacheck.commands.Get;
import fm.mox.spikes.functionaljava.scalacheck.commands.Increment;
import fm.mox.spikes.functionaljava.scalacheck.sut.Counter;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Commands<SUT, STATE, RESULT> {

    SUT newSut(STATE state);

    void destroySut(SUT counter);

    boolean initialPreCondition(STATE state);

    Gen<STATE> genInitialState();

    Gen<Command> genCommand(STATE state);

    // If you want to allow only one [[Sut]] instance to exist at any given time
    //   *  (a singleton [[Sut]]), implement this method the following way:
    boolean canCreateNewSut(STATE state,
                            Collection<STATE> initSuts,
                            Collection<SUT> runningSuts);

    default Property property(Integer threadCount, Integer maxParComb) {

        //val suts = collection.mutable.Map.empty[AnyRef,(State,Option[Sut])]
        Map<Object, P2<STATE, Option<SUT>>> suts = new HashMap<>();

        Gen<Actions<SUT, STATE, RESULT>> actions = actions(threadCount, maxParComb);
        Shrink<Actions<SUT, STATE, RESULT>> shrinkActions = shrinkActions(null);
        F<Actions<SUT, STATE, RESULT>, P1<Property>> f = null;

        Property.forall(actions, shrinkActions, f);

        //synchronized?
        Collection<P2<STATE, Option<SUT>>> values = suts.values();

        Option<Object> optSutId = sutId(suts, values);

        if (optSutId.isSome()) {
            //Some

                /*val sut = newSut(as.s)
                def removeSut():Unit = {
                        suts.synchronized {
                    suts -= id
                    destroySut(sut)
                }*/

        } else {
            //None
        }

        //verify all properties
        Increment increment = new Increment();
        Get get = new Get();
        return null;
    }

    default Shrink<Actions<SUT,STATE,RESULT>> shrinkActions(Actions<SUT,STATE,RESULT> as) {

        /*val shrinkedCmds: Stream[Actions] =
                Shrink.shrink(as.seqCmds).map(cs => as.copy(seqCmds = cs)) append
        Shrink.shrink(as.parCmds).map(cs => as.copy(parCmds = cs))

        Shrink.shrinkWithOrig[State](as.s)(shrinkState) flatMap { state =>
            shrinkedCmds.map(_.copy(s = state))
        }*/

//        Shrink.shrink(as).map();


        Shrink<Actions<SUT, STATE, RESULT>> actionsShrink = null;

        return actionsShrink;

    }

    default Gen<Actions<SUT, STATE, RESULT>> actions(Integer threadCount, Integer maxParComb) {

        int parSz = Seqs.parSz(threadCount, maxParComb);

        Gen<STATE> stateGen = genInitialState();

        Gen<P2<STATE, List<Command<SUT, STATE, RESULT>>>> stateSeqCommandsTupleGen = stateGen
            .bind(state -> Gen.sized(sizedCmdsCurry(state)));

        Gen<List<List<Command<SUT, STATE, RESULT>>>> parCmdsGen = stateSeqCommandsTupleGen.bind(stateListP2 -> {
            Gen<List<List<Command<SUT, STATE, RESULT>>>> listGen = Gen.value(List.nil());
            if (parSz > 0) {
                Gen<List<Command<SUT, STATE, RESULT>>> listOfListOfCommandGen = sizedCmdsCurry(stateListP2._1()).f(parSz).map(P2::_2);
                listGen = Gen.listOf(listOfListOfCommandGen, threadCount);
            }
            return listGen;
        });

        F<
                List<List<Command<SUT, STATE, RESULT>>>,
                F<
                        STATE, F<
                                P2<STATE, List<Command<SUT, STATE, RESULT>>>,
                                F<
                                        List<List<Command<SUT, STATE, RESULT>>>,
                                        Actions<SUT, STATE, RESULT>>
                                >
                >
         > f =
                lists -> state -> stateListP2 -> (F<List<List<Command<SUT, STATE, RESULT>>>, Actions<SUT, STATE, RESULT>>) lists1 -> new Actions<>(state, stateListP2._2(), lists1);

        return parCmdsGen.bind(stateGen, stateSeqCommandsTupleGen, parCmdsGen, f);
    }

    default F<Integer, Gen<P2<STATE, List<Command<SUT, STATE, RESULT>>>>> sizedCmdsCurry(STATE aState) {
        return sz -> sizedCmds(aState, sz);
    }

    default Gen<P2<STATE, List<Command<SUT, STATE, RESULT>>>> sizedCmds(STATE aState, Integer sz) {
        // a list with sz empty voids
            /*
                val l: List[Unit] = List.fill(sz)(())
                l.foldLeft(const((s,Nil:Commands))) { case (g,()) =>
                    for {
                        (s0,cs) <- g
                        c <- genCommand(s0) suchThat (_.preCondition(s0))
                    } yield (c.nextState(s0), cs :+ c)
                }
             */

        for (int i = 0; i < sz; i++) {


        }

        return null;
    }

    default Option<Object> sutId(Map<Object, P2<STATE, Option<SUT>>> suts,
                                 Collection<P2<STATE, Option<SUT>>> values) {
        Object sutId = null;
        // valuesWithNoneCounter
        java.util.List<STATE> initSuts = values
            .stream()
            .filter(longOptionP2 -> longOptionP2._2().isNone())
            .map(P2::_1)
            .collect(Collectors.toList());

        java.util.List<SUT> runningSuts = values
            .stream()
            .filter(longOptionP2 -> longOptionP2._2().isSome())
            .map(P2::_2)
            .map(counters -> counters.some())
            .collect(Collectors.toList());

        if (canCreateNewSut(null, initSuts, runningSuts)) {
            P2<STATE, Option<SUT>> tuple = P.p(null, Option.none());
            sutId = new Object();
            suts.put(sutId, tuple);
        }
        return Option.fromNull(sutId);
    }
}
