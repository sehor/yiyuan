package yiyuan.core.accountClassification;

import com.sun.nio.sctp.PeerAddressChangeNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccClaServiceImpl implements AccClaService {
    @Autowired
    AccClaRepository repository;

    @Override
    public AccCla addAccCla(AccCla accCla) {
        return repository.save(accCla);
    }

    @Override
    public AccCla getAccCla(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public AccCla updateAccCla(AccCla accCla) {
        return repository.save(accCla);
    }

    @Override
    public void deleteAccCla(AccCla accCla) {
        repository.delete(accCla);
    }

    @Override
    public void deleteAccCla(String id) {
        repository.deleteById(id);
    }

    @Override
    public AccClaPresentation createAccClaPresentation(AccCla accCla) {
        AccClaPresentation accClaPresentation = new AccClaPresentation(accCla);
        addChildren(accClaPresentation);
        return accClaPresentation;
    }

    @Override
    public String showFullPath(AccCla accCla) {

        Deque<String> deque = new LinkedList<>();
        while (accCla!=null&&!accCla.getId().equals("root")){
            deque.push(accCla.getName());
            accCla=repository.findById(accCla.getParentId()).orElse(null);
        }

        return String.join(" - ", deque);
    }

    private void addChildren(AccClaPresentation accClaPresentation) {
        List<AccCla> children = repository.findByParentId(accClaPresentation.getId());
        children.forEach(child -> {
            AccClaPresentation accClaPresentationChild = new AccClaPresentation(child);
            accClaPresentation.getChildObjects().add(accClaPresentationChild);
            addChildren(accClaPresentationChild);
        });
    }

}