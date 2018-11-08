package com.adsale.ChinaPlas.data;

import android.util.Log;

import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.MapFloor;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.utils.LogUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

public class ContentHandler extends DefaultHandler {

    private static final String TAG = "ContentHandler";
    // News
    private String nodeName;

    public ArrayList<MainIcon> mMainIcons;
    private ArrayList<MapFloor> mMapFloors;
    private ArrayList<NewsLink> mNewsLinks;
    public ArrayList<WebContent> mWebContents;
    private ArrayList<News> mNewsArrayLists;

    private News mNews;
    private MainIcon mMainIcon;
    private MapFloor mMapFloor;
    private NewsLink mNewsLink;
    private WebContent mWebContent;

    private String strData;
    private StringBuffer sb;
    private boolean isNews;
    private boolean isNewsLink;
    private boolean isWebContent;
    private boolean isMainIcon;
    private boolean isMapFloor;

    private long startTime;
    private long endTime;

    private LoadRepository mLoadRepository;

    public static ContentHandler getInstance(LoadRepository repository) {
        return new ContentHandler(repository);
    }

    private ContentHandler(LoadRepository repository) {
        mLoadRepository = repository;
    }

    /**
     * SAX解析XML
     *
     * @param <>
     * @param xmlData
     * @return
     */
    public void parseXmlWithSAX(String xmlData) {
//        FileUtils.writeFileToSD(xmlData, App.rootDir + "response.xml");

        if (xmlData == null) {
            LogUtil.e(TAG, "xmlData==null，直接返回");
        } else {
            long startTime = System.currentTimeMillis();
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                XMLReader xmlReader = factory.newSAXParser().getXMLReader();
                // 将ContentHandler 的实例设置到XMLReader 中
                xmlReader.setContentHandler(this);
                // 开始执行解析
                xmlReader.parse(new InputSource(new StringReader(xmlData)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            LogUtil.i(TAG, "parseXmlWithSAX spend time: " + (endTime - startTime) + " ms");
        }
    }

    @Override
    public void startDocument() throws SAXException {
        Log.i("startDocument------>", "解析开始");
        startTime = System.currentTimeMillis();
        sb = new StringBuffer();
        mMainIcons = new ArrayList<>();
        mMapFloors = new ArrayList<>();
        mNewsLinks = new ArrayList<>();
        mWebContents = new ArrayList<>();
        mNewsArrayLists = new ArrayList<>();
    }

    public ArrayList<News> parseNewsXml(String xmlData, ArrayList<News> list) {
        if (xmlData == null) {
            LogUtil.e(TAG, "xmlData==null，直接返回");
            return list;
        } else {
            long startTime = System.currentTimeMillis();
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                XMLReader xmlReader = factory.newSAXParser().getXMLReader();
                // 将ContentHandler 的实例设置到XMLReader 中
                xmlReader.setContentHandler(this);
                // 开始执行解析
                xmlReader.parse(new InputSource(new StringReader(xmlData)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            LogUtil.i(TAG, "parseNewsXml spend time: " + (endTime - startTime) + " ms");
            return mNewsArrayLists;
        }
    }


    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        sb.delete(0, sb.length());
        nodeName = localName;

        if ("News".equals(localName)) {
            mNews = new News();
            isNews = true;
        } else if (localName.equals("NewsLink")) {
            mNewsLink = new NewsLink();
            isNewsLink = true;
        } else if (localName.equals("WebContent")) {
            mWebContent = new WebContent();
            isWebContent = true;
        } else if (localName.equals("MainIcon")) {
            mMainIcon = new MainIcon();
            isMainIcon = true;
        } else if (localName.equals("MapFloor")) {
            mMapFloor = new MapFloor();
            isMapFloor = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        sb.append(ch, start, length);
        strData = sb.toString();

        // -----------------MainIcon--------------------
        if (isMainIcon) {
            if (nodeName.equals("IsDelete")) {
                mMainIcon.setIsDelete(strData.equals("false") ? 0 : 1);
            } else if ("IconID".equals(nodeName)) {
                mMainIcon.setIconID(strData);
            } else if ("CType".equals(nodeName)) {
                mMainIcon.setCType(Integer.valueOf(strData));
            } else if ("SEQ".equals(nodeName)) {
                mMainIcon.setSEQ(Integer.valueOf(strData));
            } else if ("IsHidden".equals(nodeName)) {
                strData = strData.toLowerCase().equals("false") ? "0" : "1";
                mMainIcon.setIsHidden(Integer.valueOf(strData));
            } else if ("TitleTW".equals(nodeName)) {
                mMainIcon.setTitleTW(strData);
            } else if ("TitleCN".equals(nodeName)) {
                mMainIcon.setTitleCN(strData);
            } else if ("TitleEN".equals(nodeName)) {
                mMainIcon.setTitleEN(strData);
            } else if ("Icon".equals(nodeName)) {
                mMainIcon.setIcon(strData);
            } else if ("CFile".equals(nodeName)) {
                mMainIcon.setCFile(strData);
            } else if ("ZipDateTime".equals(nodeName)) {
                mMainIcon.setZipDateTime(strData);
            } else if ("BaiDu_TJ".equals(nodeName)) {
                mMainIcon.setBaiDu_TJ(strData);
            } else if ("Google_TJ".equals(nodeName)) {
                mMainIcon.setGoogle_TJ(strData);
                if (strData.contains("S_")) {
                    mMainIcon.setDrawerList(strData.split("\\|")[0]);
                }
                if (strData.toLowerCase().contains("icon")) {
                    mMainIcon.setDrawerIcon(strData.split("\\|")[3]);
                }
                if (strData.contains("M_")) {
                    mMainIcon.setMenuList(strData.split("\\|")[1]);
                }
                if (strData.contains("|") && strData.split("\\|").length > 2) {
                    mMainIcon.setIconTextColor(strData.split("\\|")[2]);
                }
//                LogUtil.i(TAG,"drawerList="+ mMainIcon.getDrawerList()+",drawerIcon="+mMainIcon.getDrawerIcon()+",menuList="+mMainIcon.getMenuList());

            } else if (nodeName.equals("CreateDateTime")) {
                mMainIcon.setCreateDateTime(strData);
            } else if (nodeName.equals("UpdateDateTime")) {
                mMainIcon.setUpdateDateTime(strData);
            } else if (nodeName.equals("RecordTimeStamp")) {
                mMainIcon.setRecordTimeStamp(strData);
            }
            mMainIcon.setIsDown(0);
        }

        // -----------------NewsLink--------------------
        if (isNewsLink) {
            if ("LinkID".equals(nodeName)) {
                mNewsLink.setLinkID(strData);
            } else if ("NewsID".equals(nodeName)) {
                mNewsLink.setNewsID(strData);
            } else if ("Photo".equals(nodeName)) {
                mNewsLink.setPhoto(strData);
            } else if ("Link".equals(nodeName)) {
                mNewsLink.setLink(strData);
            } else if ("Title".equals(nodeName)) {
                mNewsLink.setTitle(strData);
            } else if (nodeName.equals("CreateDateTime")) {
                mNewsLink.setCreateDateTime(strData);
            } else if (nodeName.equals("UpdateDateTime")) {
                mNewsLink.setUpdateDateTime(strData);
            } else if (nodeName.equals("RecordTimeStamp")) {
                mNewsLink.setRecordTimeStamp(strData);
            }
        }

        // -----------------News--------------------
        if (isNews) {
            if ("LType".equals(nodeName)) {
                mNews.setLType(Integer.parseInt(strData));
            } else if ("NewsID".equals(nodeName)) {
                mNews.setNewsID(strData);
            } else if ("Description".equals(nodeName)) {
                mNews.setDescription(strData);
            } else if ("Logo".equals(nodeName)) {
                mNews.setLogo(strData);
            } else if ("Title".equals(nodeName)) {
                mNews.setTitle(strData);
            } else if ("ShareLink".equals(nodeName)) {
                mNews.setShareLink(strData);
            } else if ("PublishDate".equals(nodeName)) {
                mNews.setPublishDate(strData);
            } else if (nodeName.equals("CreateDateTime")) {
                mNews.setCreateDateTime(strData);
            } else if (nodeName.equals("UpdateDateTime")) {
                mNews.setUpdateDateTime(strData);
            } else if (nodeName.equals("RecordTimeStamp")) {
                mNews.setRecordTimeStamp(strData);
            }
        }
        // -----------------MapFloor--------------------
        if (isMapFloor) {
            if ("MapFloorID".equals(nodeName)) {
                mMapFloor.setMapFloorID(strData);
            } else if ("SEQ".equals(nodeName)) {
                mMapFloor.setSEQ(Integer.parseInt(strData));
            } else if ("Type".equals(nodeName)) {
                mMapFloor.setType(Integer.parseInt(strData));
            } else if ("ParentID".equals(nodeName)) {
                mMapFloor.setParentID(strData);
            } else if ("NameTW".equals(nodeName)) {
                mMapFloor.setNameTW(strData);
            } else if ("NameCN".equals(nodeName)) {
                mMapFloor.setNameCN(strData);
            } else if ("NameEN".equals(nodeName)) {
                mMapFloor.setNameEN(strData);
            } else if (nodeName.equals("CreateDateTime")) {
                mMapFloor.setCreateDateTime(strData);
            } else if (nodeName.equals("UpdateDateTime")) {
                mMapFloor.setUpdateDateTime(strData);
            } else if (nodeName.equals("RecordTimeStamp")) {
                mMapFloor.setRecordTimeStamp(strData);
            }
            mMapFloor.setDown(0);
        }

        // ------------WebContent-------------------
        if (isWebContent) {
            if ("PageId".equals(nodeName)) {
                mWebContent.setPageId(strData);
            } else if ("CType".equals(nodeName)) {
                mWebContent.setCType(Integer.parseInt(strData));
            } else if ("TitleTW".equals(nodeName)) {
                mWebContent.setTitleTW(strData);
            } else if ("TitleCN".equals(nodeName)) {
                mWebContent.setTitleCN(strData);
            } else if ("TitleEN".equals(nodeName)) {
                mWebContent.setTitleEN(strData);
            } else if ("CFile".equals(nodeName)) {
                mWebContent.setCFile(strData);
            } else if ("ZipDateTime".equals(nodeName)) {
                mWebContent.setZipDateTime(strData);
            } else if ("ContentEN".equals(nodeName)) {
                mWebContent.setContentEN(strData);
            } else if ("ContentSC".equals(nodeName)) {
                //	LogUtil.i(TAG, "contentSC="+strData);
                mWebContent.setContentSC(strData);
            } else if ("ContentTC".equals(nodeName)) {
                mWebContent.setContentTC(strData);
            } else if (nodeName.equals("CreateDateTime")) {
                mWebContent.setCreateDateTime(strData);
            } else if (nodeName.equals("UpdateDateTime")) {
                mWebContent.setUpdateDateTime(strData);
            } else if (nodeName.equals("RecordTimeStamp")) {
                mWebContent.setRecordTimeStamp(strData);
            }
            mWebContent.setIsDown(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if ("News".equals(localName)) {
            mNewsArrayLists.add(mNews);
            isNews = false;
        } else if (localName.equals("NewsLink")) {
            mNewsLinks.add(mNewsLink);
            isNewsLink = false;
        } else if (localName.equals("WebContent")) {
            mWebContents.add(mWebContent);
            isWebContent = false;
        } else if (localName.equals("MainIcon")) {
            if (mMainIcon.getBaiDu_TJ() != null) {
                mMainIcons.add(mMainIcon);
            }
            isMainIcon = false;
        } else if (localName.equals("MapFloor")) {
            mMapFloors.add(mMapFloor);
            isMapFloor = false;
        }
        nodeName = null;
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LogUtil.i("ContentHandler", "解析完成！！！！！！！");
        logList(mMainIcons, "MainIcon");
        logList(mNewsArrayLists, "mNewsArrayLists");
        logList(mNewsLinks, "mNewsLinks");
        logList(mNewsArrayLists, "mNewsArrayLists");
        logList(mWebContents, "mWebContents");
        logList(mMapFloors, "mMapFloors");

        endTime = System.currentTimeMillis();
        Log.i(TAG, "解析耗时：" + (endTime - startTime) + "ms");

        //解析完成，插入数据库
        mLoadRepository.prepareInsertXmlData();
        mLoadRepository.insertMainIconAll(mMainIcons);
        mLoadRepository.insertNewsAll(mNewsArrayLists);
        mLoadRepository.insertNewsLinkAll(mNewsLinks);
        mLoadRepository.insertWebContentAll(mWebContents);
        mLoadRepository.insertMapFloorAll(mMapFloors);
        mLoadRepository.setLUT();

    }

    private <T> void logList(ArrayList<T> list, String tag) {
        if (list.size() > 0) {
            LogUtil.i("ContentHandler", "解析完成！！！！！！！" + list.size() + ",,," + tag + " == ");   //+ list.toString()
        }
    }


}
