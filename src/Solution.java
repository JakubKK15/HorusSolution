import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

interface Structure {
  // zwraca dowolny element o podanym kolorze
  Optional<Block> findBlockByColor(String color);

  // zwraca wszystkie elementy z danego materiału
  List<Block> findBlocksByMaterial(String material);

  //zwraca liczbę wszystkich elementów tworzących strukturę
  int count();
}

interface Block {
  String color();

  String material();
}

interface CompositeBlock extends Block {
  List<Block> blocks();
}

class Wall implements Structure {
  private final List<Block> blocks;

  public Wall(List<Block> blocks) {
    this.blocks = blocks;
  }

  @Override
  public Optional<Block> findBlockByColor(String color) {

    // Szukam bloku o podanym kolorze we wszystkich blokach
    for (Block block : blocks) {
      if (block.color().equals(color)) {
        return Optional.of(block);
      }
      /* Jeśli blok jest typu CompositeBlock, rekurencyjnie szukam w jego
          zagnieżdżonych blokach
       */
      if (block instanceof CompositeBlock) {
        Optional<Block> nestedBlock = findBlockInCompositeByColor(
            (CompositeBlock) block, color);
        if (nestedBlock.isPresent()) {
          return nestedBlock;
        }
      }
    }
    /*
    Jesli nie znajde bloku o podanym kolorze, zwracam pusta wartosc opcjonalna
     */
    return Optional.empty();
  }

  private Optional<Block> findBlockInCompositeByColor(
      CompositeBlock compositeBlock, String color) {

    List<Block> nestedBlocks = compositeBlock.blocks();
    /*
    Przeszukuje zagniezdzone bloki, w poszukiwaniu bloku o podanym kolorze
     */
    for (Block nestedBlock : nestedBlocks) {
      if (nestedBlock.color().equals(color)) {
        return Optional.of(nestedBlock);
      }
      /*
      Jesli zagniezdzony blok jest typu CompositeBlock szukam rekurencyjnie w
       jego zagniezdzonych blokach.
       */
      if (nestedBlock instanceof CompositeBlock) {
        Optional<Block> nestedNestedBlock = findBlockInCompositeByColor(
            (CompositeBlock) nestedBlock, color);
        if (nestedNestedBlock.isPresent()) {
          return nestedNestedBlock;
        }
      }
    }
    /*
    Jesli nie znajde bloku o podanym kolorze, zwracam pusta wartosc opcjonalna
     */
    return Optional.empty();
  }

  @Override
  public List<Block> findBlocksByMaterial(String material) {

    List<Block> matchingBlocks = new ArrayList<>();
    /*
    Przeszukuje bloki w poszukiwaniu blokow z podanego materialu
     */
    for (Block block : blocks) {
      if (block.material().equals(material)) {
        matchingBlocks.add(block);
      }
      /*
      Jesli blok jest typu CompositeBlock, rekurencyjnie szukam w jego
      zagniezdzonych blokach.
       */
      if (block instanceof CompositeBlock) {
        List<Block> nestedMatchingBlocks = findBlocksInCompositeByMaterial(
            (CompositeBlock) block, material);
        matchingBlocks.addAll(nestedMatchingBlocks);
      }
    }
    /*
    Zwracam liste znalezionych blokow.
     */
    return matchingBlocks;
  }

  private List<Block> findBlocksInCompositeByMaterial(
      CompositeBlock compositeBlock, String material) {

    List<Block> matchingBlocks = new ArrayList<>();
    List<Block> nestedBlocks = compositeBlock.blocks();
    /*
    Przeszukuje zagniezdzone bloki w poszukiwaniu blokow z podanego materialu.
     */
    for (Block nestedBlock : nestedBlocks) {
      if (nestedBlock.material().equals(material)) {
        matchingBlocks.add(nestedBlock);
      }
      /*
      Jesli zagniezdzony blok jest typu CompositeBlock rekurencyjnie szukam w
       jego zagniezdzonych blokach.
       */
      if (nestedBlock instanceof CompositeBlock) {
        List<Block> nestedMatchingBlocks = findBlocksInCompositeByMaterial(
            (CompositeBlock) nestedBlock, material);
        matchingBlocks.addAll(nestedMatchingBlocks);
      }
    }
    /*
    Zwracam liste znalezionych blokow.
     */
    return matchingBlocks;
  }


  @Override
  public int count() {
    int count = 0;
    /*
    Zliczam wszystkie bloki tworzace strukture.
     */
    for (Block block : blocks) {
      count++;
      /*
      Jesli blok jest typu CompositeBlock, rekurencyjnie zliczam ilosc jego
      zagniezdzonych blokow.
       */
      if (block instanceof CompositeBlock) {
        count += countBlocksInComposite((CompositeBlock) block);
      }
    }
    /*
    Zwracam liczbe blokow.
     */
    return count;
  }

  private int countBlocksInComposite(CompositeBlock compositeBlock) {
    int count = 0;
    List<Block> nestedBlocks = compositeBlock.blocks();
    /*
    Zliczam bloki zagniezdzone w bloku typu CompositeBlock
     */
    for (Block nestedBlock : nestedBlocks) {
      count++;
      /*
      Jesli zagniezdzony blok jest typu CompositeBlock, rekurencyjnie zliczam
       ilosc jego zagniezdzonych blokow.
       */
      if (nestedBlock instanceof CompositeBlock) {
        count += countBlocksInComposite(
            (CompositeBlock) nestedBlock);
      }
    }
    /*
    Zwracam liczbe blokow.
     */
    return count;
  }
}

record SimpleBlock(String color, String material) implements Block {
}

record CompositeBlockImpl(List<Block> blocks) implements CompositeBlock {

  @Override
  public String color() {
    return blocks.get(0).color();
  }

  @Override
  public String material() {
    return blocks.get(0).material();
  }
}