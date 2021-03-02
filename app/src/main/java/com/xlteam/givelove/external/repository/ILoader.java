package com.xlteam.givelove.external.repository;
import java.util.List;

public interface ILoader<CaptionType> {
    void loadResult(int loaderTaskType, List<CaptionType> result);
}
